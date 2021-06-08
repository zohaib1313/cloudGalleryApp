package com.ladstech.cloudgalleryapp.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.amplifyframework.datastore.generated.model.Comments
import com.ladstech.cloudgalleryapp.adapters.AdapterComments
import com.ladstech.cloudgalleryapp.databinding.ActivityPostDetailBinding

class PostDetailActivity :BaseActivity() {

    private lateinit var mBinding: ActivityPostDetailBinding
    private lateinit var adapterComments: AdapterComments
    private var dataListComments = ArrayList<Comments>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild

        ///get post from intent


        initRv()
    }

    private fun initRv() {
        adapterComments = AdapterComments(this@PostDetailActivity, dataListComments)
        mBinding.rvComments.layoutManager = LinearLayoutManager(this)
        mBinding.rvComments.setHasFixedSize(true)
        mBinding.rvComments.adapter = adapterComments
        adapterComments.notifyDataSetChanged()
        ///get load comments

    }
}