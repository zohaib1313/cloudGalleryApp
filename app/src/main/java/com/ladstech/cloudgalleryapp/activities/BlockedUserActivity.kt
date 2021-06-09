package com.ladstech.cloudgalleryapp.activities


import android.graphics.drawable.Drawable
import android.os.Bundle

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amplifyframework.datastore.generated.model.BlockedUsers
import com.amplifyframework.datastore.generated.model.Comments
import com.ladstech.cloudgalleryapp.R

import com.ladstech.cloudgalleryapp.adapters.AdapterBlockedUsers
import com.ladstech.cloudgalleryapp.databinding.ActivityBlockedUserBinding


class BlockedUserActivity : BaseActivity() {

    private lateinit var mBinding: ActivityBlockedUserBinding
    private lateinit var adapterBlockedUsers: AdapterBlockedUsers
    private var dataListBlockedUsers = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBlockedUserBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild

        ///get post from intent
        initRv()


    }

    private fun initRv() {

        adapterBlockedUsers = AdapterBlockedUsers(this@BlockedUserActivity, dataListBlockedUsers)
        mBinding.staggeredGridView.layoutManager =  StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        mBinding.staggeredGridView.setHasFixedSize(true)
        mBinding.staggeredGridView.adapter = adapterBlockedUsers
        adapterBlockedUsers.notifyDataSetChanged()

//        val blockedUsers=BlockedUsers.builder().blockBy(sessionManager.user)
//            .blockTo(sessionManager.user)
//            .build()

        dataListBlockedUsers.add(R.drawable.a)
        dataListBlockedUsers.add(R.drawable.b)
        dataListBlockedUsers.add(R.drawable.c)
        dataListBlockedUsers.add(R.drawable.d)
        dataListBlockedUsers.add(R.drawable.e)
        dataListBlockedUsers.add(R.drawable.g)
    }
}