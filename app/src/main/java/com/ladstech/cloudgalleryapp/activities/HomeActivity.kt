package com.ladstech.cloudgalleryapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Posts
import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.adapters.AdapterPosts
import com.ladstech.cloudgalleryapp.databinding.ActivityHomeBinding
import com.ladstech.cloudgalleryapp.utils.Helper
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.row_posts.view.*


class HomeActivity : BaseActivity() {

    private lateinit var mBinding: ActivityHomeBinding
    private lateinit var adapterPosts: AdapterPosts
    private var dataListPosts = ArrayList<Posts>()
    var popupWindow: PopupWindow? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
//        loadingLayout = mBinding.loadin
//         gLayout.rlLoading
//        noDataFoundLayout = mBinding.noDataLayout.noDataChild

        mBinding.ivNav.setOnClickListener { openLeftMenu() }
        mBinding.ivBlockedUsers.setOnClickListener {
            Helper.startActivity(
                this@HomeActivity,
                Intent(this@HomeActivity, BlockedUserActivity::class.java),
                false
            )
        }

        mBinding.navContent.btnAcceptRequst.setOnClickListener {
            openLeftMenu()
            showCustomAppAlert(this, R.layout.accept_request_layout)
        }
        mBinding.navContent.btnVerifyOtp.setOnClickListener {
            openLeftMenu()
            showCustomAppAlert(this, R.layout.verify_otp_layout)
        }
        mBinding.navContent.btnCloseApp.setOnClickListener {
            openLeftMenu()
            showCustomAppAlert(this, R.layout.close_layout)
        }
        mBinding.navContent.btnSendRequest.setOnClickListener {
            openLeftMenu()
            showCustomAppAlert(this, R.layout.send_request_layout)
        }

        mBinding.navContent.btnLogout.setOnClickListener {
          sessionManager.clearSession()
            sessionManager.isLoggedIn=false
            Helper.startActivity(this@HomeActivity,Intent(this@HomeActivity,SplashActivity::class.java),true)

        }


        setUpPopWindow()
        initRv()


    }
    fun PopupWindow.dimBehind() {
        val container = contentView.rootView
        val context = contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = p.flags or WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.3f
        wm.updateViewLayout(container, p)
    }
    fun showCustomAppAlert(activity: Activity, view: Int) {

        val layout = this.layoutInflater.inflate(
            view,
            this.findViewById(R.id.container)
        )
        val dialog = AlertDialog.Builder(activity)
            .setView(layout)
            .create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

    }

    private fun setUpPopWindow() {
        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = layoutInflater.inflate(R.layout.popup_filter_layout, null)
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow?.let { popupWindow ->
            popupWindow.elevation=8.0f

            popupWindow.setBackgroundDrawable(ColorDrawable())
            popupWindow.isOutsideTouchable = true
            popupWindow.setOnDismissListener(PopupWindow.OnDismissListener {
            })
            popupWindow.contentView.findViewById<ImageView>(R.id.ivLike).setOnClickListener {
                printLog("like")
                popupWindow.dismiss()
            }
            popupView.findViewById<ImageView>(R.id.ivComment).setOnClickListener {
                printLog("comments")
                popupWindow.dismiss()
            }
            popupView.findViewById<ImageView>(R.id.ivShare).setOnClickListener {
                printLog("share")
                popupWindow.dismiss()

            }
        }
    }

    private fun initRv() {
        adapterPosts = AdapterPosts(this@HomeActivity, dataListPosts)
        mBinding.rvPost.layoutManager = LinearLayoutManager(this)
        mBinding.rvPost.setHasFixedSize(true)
        mBinding.rvPost.adapter = adapterPosts
        adapterPosts.notifyDataSetChanged()
        mBinding.rvPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                popupWindow?.let { if (it.isShowing) it.dismiss() }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

            }
        })
        adapterPosts.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {

                when (view.id) {
                    R.id.btnDownloadPost -> {

                        //download

                    }
                    R.id.ivUser -> {
                        ///see user profile
                    }
                    R.id.btnMorePost -> {
                        //see options

                        popupWindow?.let { popupWindow ->
                            if (popupWindow.isShowing) {
                                popupWindow.dismiss()
                            }
                            popupWindow.showAsDropDown(view,-view.width*2,-3*view.height)
                            popupWindow.dimBehind()
                        }
                    }

                    R.id.imageView5 -> {
                        //see post details
//                          gotoPostDetailActivity(dataListPosts[position])
                        Helper.startActivity(
                            this@HomeActivity,
                            Intent(this@HomeActivity, PostDetailActivity::class.java),
                            false
                        )

                    }
                }

            }
        })


    }

    private fun openLeftMenu() {
        Helper.hideKeyboard(this)
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            mBinding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }


    private fun gotoPostDetailActivity(posts: Posts) {

        Helper.startActivity(
            this@HomeActivity,
            Intent(this@HomeActivity, PostDetailActivity::class.java),
            false
        )

    }


}