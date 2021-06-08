package com.ladstech.cloudgalleryapp.activities

import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild
        initRv()


    }

    private fun initRv() {
        adapterPosts = AdapterPosts(this@HomeActivity, dataListPosts)
        mBinding.rvPost.layoutManager = LinearLayoutManager(this)
        mBinding.rvPost.setHasFixedSize(true)
        mBinding.rvPost.adapter = adapterPosts
        adapterPosts.notifyDataSetChanged()

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
                        showPopup(view)
                    }

                    R.id.imageView5 -> {
                        //see post details
                       //  gotoPostDetailActivity(dataListPosts[position])
                        Helper.startActivity(this@HomeActivity, Intent(this@HomeActivity,PostDetailActivity::class.java),false)

                    }
                }

            }
        })


    }

    private fun gotoPostDetailActivity(posts: Posts) {

        Helper.startActivity(this@HomeActivity, Intent(this@HomeActivity,PostDetailActivity::class.java),false)

    }


    fun showPopup(v: View?) {

        val layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = layoutInflater.inflate(R.layout.popup_filter_layout, null)
       val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.setBackgroundDrawable(ColorDrawable())
        popupWindow.isOutsideTouchable = true
        popupWindow.setOnDismissListener(PopupWindow.OnDismissListener {
            //TODO do sth here on dismiss
        })
        popupWindow.contentView.findViewById<ImageView>(R.id.ivLike).setOnClickListener { v?.context

        printLog("like")
        popupWindow.dismiss()
        }
        popupView.findViewById<ImageView>(R.id.ivComment).setOnClickListener {   printLog("comments")
            popupWindow.dismiss()
        }
        popupView.findViewById<ImageView>(R.id.ivShare).setOnClickListener {   printLog("share")
            popupWindow.dismiss()

        }

        popupWindow.showAsDropDown(v,0,0,Gravity.TOP)
    }


}