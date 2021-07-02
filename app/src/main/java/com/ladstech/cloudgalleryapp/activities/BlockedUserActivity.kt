package com.ladstech.cloudgalleryapp.activities


import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View

import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelPagination
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.BlockedUsers
import com.amplifyframework.datastore.generated.model.Comments
import com.amplifyframework.datastore.generated.model.Connections
import com.amplifyframework.datastore.generated.model.UserCloudGallery
import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.google.gson.Gson
import com.ladstech.cloudgalleryapp.R

import com.ladstech.cloudgalleryapp.adapters.AdapterBlockedUsers
import com.ladstech.cloudgalleryapp.databinding.ActivityBlockedUserBinding
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.Helper


class BlockedUserActivity : BaseActivity() {

    private lateinit var mBinding: ActivityBlockedUserBinding
    private lateinit var adapterBlockedUsers: AdapterBlockedUsers
    private var dataListBlockedConnections = ArrayList<BlockedUsers>()
    private var dataListBlockedUsers = ArrayList<UserCloudGallery>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityBlockedUserBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild


        sessionManager.user?.let {
            initRv()
        }

    }

    private fun initRv() {

        adapterBlockedUsers = AdapterBlockedUsers(this@BlockedUserActivity, dataListBlockedUsers)
        mBinding.staggeredGridView.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        mBinding.staggeredGridView.setHasFixedSize(true)
        mBinding.staggeredGridView.adapter = adapterBlockedUsers
        adapterBlockedUsers.setOnItemClickListener(object :OnItemClickListener{
            override fun onItemClick(view: View, position: Int, character: String) {

                Helper.startActivity(
                    this@BlockedUserActivity,
                    Intent(
                        this@BlockedUserActivity,
                        ProfileDetailsWatchActivity::class.java
                    ).apply {
                        putExtra(
                            AppConstant.KEY_DATA,
                            Gson().toJson(dataListBlockedUsers[position])
                        )
                    },
                    false
                )

           }
        })
        adapterBlockedUsers.notifyDataSetChanged()

        queryBlockedUser(
            ModelQuery.list(
                BlockedUsers::class.java,
                ModelPagination.limit(1_000)
            )
        )
    }

    private fun queryBlockedUser(request: GraphQLRequest<PaginatedResult<BlockedUsers>>) {
        dataListBlockedUsers.clear()
        ThreadUtils.runOnUiThread {
            showLoading()
        }

        Amplify.API.query(request,
            { response ->
                if (response.hasData()) {

                    response.data.items.forEach { blockedUsers ->
                        dataListBlockedConnections.add(blockedUsers)
                    }

                    if (response.data.hasNextResult()) {
                        queryBlockedUser(response.data.requestForNextResult)
                    } else {

                        dataListBlockedConnections.forEach { blockedUser ->
                            if (blockedUser.blockBy.id == sessionManager.user.id) {
                                dataListBlockedUsers.add(blockedUser.blockTo)
                                printLog("blocked user added")
                            }
                        }
                        ThreadUtils.runOnUiThread {
                            if (dataListBlockedUsers.isEmpty()) {
                                showNoDataLayout()
                            }
                            hideLoading()
                            adapterBlockedUsers.notifyDataSetChanged()
                        }

                    }
                }
            },
            {
                Log.e("MyAmplifyApp", "Query failed", it)
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }
            })


    }
}