package com.ladstech.cloudgalleryapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amplifyframework.datastore.generated.model.BlockedUsers
import com.amplifyframework.datastore.generated.model.Comments
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.adapters.AdapterBlockedUsers
import com.ladstech.cloudgalleryapp.adapters.AdapterComments
import com.ladstech.cloudgalleryapp.databinding.ActivityBlockedUserBinding


class BlockedUserActivity : BaseActivity() {

    private lateinit var mBinding: ActivityBlockedUserBinding
    private lateinit var adapterBlockedUsers: AdapterBlockedUsers
    private var dataListBlockedUsers = ArrayList<BlockedUsers>()

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
    }
}