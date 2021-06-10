package com.ladstech.cloudgalleryapp.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
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
//        loadingLayout = mBinding.loadingLayout.rlLoading
//        noDataFoundLayout = mBinding.noDataLayout.noDataChild

        mBinding.ivNav.setOnClickListener { openLeftMenu() }
        mBinding.navContent.btnBlockedUser.setOnClickListener {
            openLeftMenu()
            Helper.startActivity(
                this@HomeActivity,
                Intent(this@HomeActivity, BlockedUserActivity::class.java),
                false
            )
        }
        mBinding.navContent.btnSendRequest.setOnClickListener {
            openLeftMenu()
            showCustomAppAlert(this, R.layout.send_request_layout)
        }
        mBinding.navContent.btnAcceptRequst.setOnClickListener {
            openLeftMenu()
            showCustomAppAlert(this, R.layout.accept_request_layout)
        }
        mBinding.navContent.btnVerifyOtp.setOnClickListener {
            openLeftMenu()
            showCustomAppAlert(this, R.layout.verify_otp_layout)
        }

        setUpPopWindow()
        initRv()


    }

    fun showCustomAppAlert(activity: Activity, view: Int) {

        val layout = this.layoutInflater.inflate(
            view,
            this.findViewById(R.id.container)
        )
        val dialog = AlertDialog.Builder(activity)
            .setView(layout)
            .create()
        //  val layoutParams = dialog.window!!.attributes
        //  layoutParams.y = 160 // top margin
        //dialog.window!!.attributes = layoutParams
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        // dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        // dialog.window!!.setGravity(Gravity.TOP)
        dialog.show()
//        val timer2 = Timer()
//        timer2.schedule(object : TimerTask() {
//            override fun run() {
//                dialog.dismiss()
//                timer2.cancel() //this will cancel the timer of the system
//            }
//        }, 2000)
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
                        //onSingleSectionWithIconsClicked(view)
                        popupWindow?.let { popupWindow ->
                            if (popupWindow.isShowing) {
                                popupWindow.dismiss()
                            }
                            popupWindow.showAsDropDown(view, 0, 0, Gravity.TOP)
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