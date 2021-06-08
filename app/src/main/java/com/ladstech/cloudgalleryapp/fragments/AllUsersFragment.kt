package com.ladstech.cloudgalleryapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.BlockedUsers
import com.amplifyframework.datastore.generated.model.Connections
import com.amplifyframework.datastore.generated.model.UserCloudGallery
import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.adapters.AdapterAllUsers
import com.ladstech.cloudgalleryapp.databinding.FragmentAllUsersBinding
import com.ladstech.cloudgalleryapp.models.ModelAdapeterConnectionsCloudUserInfo
import com.ladstech.cloudgalleryapp.utils.AppConstant
import kotlinx.android.synthetic.main.row_user.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AllUsersFragment : BaseFragment() {

    lateinit var mBinding: FragmentAllUsersBinding
    private lateinit var adapterAllUsers: AdapterAllUsers
    private var dataListUsers = ArrayList<ModelAdapeterConnectionsCloudUserInfo>()
    private var dataLisConnections = ArrayList<Connections>()
    private var dataListBlockedUsers = ArrayList<BlockedUsers>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentAllUsersBinding.inflate(layoutInflater)
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild

        initRv()
        return mBinding.root
    }

    private fun initRv() {
        mBinding.rvUsers.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvUsers.setHasFixedSize(true)
        adapterAllUsers = AdapterAllUsers(requireContext(), dataListUsers)
        mBinding.rvUsers.adapter = adapterAllUsers
        adapterAllUsers.notifyDataSetChanged()

        adapterAllUsers.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {
                if (view.id == R.id.btn_follow) {
                    val user = dataListUsers[position]
                    when (user.connectionStatus) {
                        "" -> {
                            dataListUsers[position] =
                                dataListUsers[position].apply {
                                    connectionStatus = AppConstant.PENDING
                                }
                            adapterAllUsers.notifyDataSetChanged()
                            followToUser(user)
                        }
                        AppConstant.REJECTED-> {
                            dataListUsers[position] =
                                dataListUsers[position].apply {
                                    connectionStatus = AppConstant.PENDING
                                }
                            adapterAllUsers.notifyDataSetChanged()
                            resendFollowRequest(user)
                        }
                        AppConstant.PENDING -> {
                            dataListUsers[position] =
                                dataListUsers[position].apply { connectionStatus = "" }
                            adapterAllUsers.notifyDataSetChanged()
                            deleteConnectionRequest(user)
                        }
                        AppConstant.ACCEPTED -> {
                            //view profile
                        }


                    }


                }


            }
        })
        showLoading()
        GlobalScope.launch(Dispatchers.Main) { // launches coroutine in main thread
            printLog("getting list connections")
            getListOfConnections()
        }


    }

    private fun resendFollowRequest(userAdapeterConnections: ModelAdapeterConnectionsCloudUserInfo) {
        showLoading()
        val followers = Connections.Builder()
            .status(AppConstant.PENDING)
            .followBy(sessionManager.user)
            .followTo(userAdapeterConnections.userCloudGallery)
            .build()
        Amplify.API.mutate(ModelMutation.update(followers), {
            if (it.hasErrors()) {
                ThreadUtils.runOnUiThread() {
                    hideLoading()
                    Toast.makeText(
                        requireContext(), "sending follow request failed", Toast
                            .LENGTH_LONG
                    ).show()
                }
                return@mutate
            } else {
                printLog("connect request ressent")
                ThreadUtils.runOnUiThread() {
                    hideLoading()
                }
            }
        }, {
            printLog("connect request sending failed")
            ThreadUtils.runOnUiThread() {
                hideLoading()
            }
        })

    }

    private fun deleteConnectionRequest(userAdapeterConnections: ModelAdapeterConnectionsCloudUserInfo) {

        showLoading()
        val request = ModelQuery.list(Connections::class.java)
        Amplify.API.query(request, { response ->
            response.data.forEach {
                if (it.followTo.id == userAdapeterConnections.userCloudGallery.id && sessionManager.user.id == it.followBy.id) {
                    Amplify.API.mutate(ModelMutation.delete(it), {
                        ThreadUtils.runOnUiThread {
                            hideLoading()
                            printLog("connection request removed")
                        }
                    }, {
                        ThreadUtils.runOnUiThread {
                            hideLoading()
                            printLog("connection request remove failure")
                        }
                    })


                }

            }
        }, {
            printLog("error getting list of blocked users")

        })

    }

    private fun getListOfBlockedUsers() {
        val request = ModelQuery.list(Connections::class.java)
        Amplify.API.query(request, { response ->
            response.data?.let {
                dataLisConnections.addAll(it)
            }

            getListOfUsers()
        }, {
            printLog("error getting list of blocked users")

        })
    }

    private fun getListOfConnections() {
        val request = ModelQuery.list(BlockedUsers::class.java)
        Amplify.API.query(request, { response ->
            response.data?.let {
                dataListBlockedUsers.addAll(it)
            }

            printLog("getting list of blocked users")
            getListOfBlockedUsers()
        }, {})
    }

    private fun followToUser(userAdapeterConnections: ModelAdapeterConnectionsCloudUserInfo) {
        showLoading()
        val followers = Connections.Builder()
            .status(AppConstant.PENDING)
            .followBy(sessionManager.user)
            .followTo(userAdapeterConnections.userCloudGallery)
            .build()
        Amplify.API.mutate(ModelMutation.create(followers), {
            if (it.hasErrors()) {
                ThreadUtils.runOnUiThread() {
                    hideLoading()
                    Toast.makeText(
                        requireContext(), "sending follow request failed", Toast
                            .LENGTH_LONG
                    ).show()
                }
                return@mutate
            } else {
                ///table entry created for followers
                printLog("connect request sent")
                ThreadUtils.runOnUiThread() {
                    hideLoading()

                }
            }
        }, {
            printLog("connect request sending failed")
            ThreadUtils.runOnUiThread() {
                hideLoading()
            }
        })


        //   Amplify.API.mutate(ModelMutation.create(user), {}, {})


    }

    private fun getListOfUsers() {
        printLog("getting list of userss info")
        ThreadUtils.runOnUiThread {
            dataListUsers.clear()
            adapterAllUsers.notifyDataSetChanged()
            showLoading()
        }
        ///reading one time
        val request =
            ModelQuery.list(UserCloudGallery::class.java)
        Amplify.API.query(request, { response ->
            if (response.data != null) {
                response.data.forEach { user ->
                    if (user.id != sessionManager.user.id) {
                        checkConnectionStatus(user)
                    }
                }
                ThreadUtils.runOnUiThread {
                    hideLoading()
                    adapterAllUsers.notifyDataSetChanged()
                }

            } else {
                printLog("no posts")
                ThreadUtils.runOnUiThread { hideLoading() }

            }
        }, {
            printLog("error getting posts")

            ThreadUtils.runOnUiThread { hideLoading() }
        })


    }

    private fun checkConnectionStatus(user: UserCloudGallery) {
        val modelCloudUserInfo = ModelAdapeterConnectionsCloudUserInfo()
        modelCloudUserInfo.userCloudGallery = user
        modelCloudUserInfo.connectionStatus = ""
        modelCloudUserInfo.isBlocked = false
        dataLisConnections.forEach {
            if (it.followBy.id == sessionManager.user.id && it.followTo.id == user.id) {
                modelCloudUserInfo.connectionStatus = it.status
            }
        }

        checkIfIsBlocked(modelCloudUserInfo)
    }

    private fun checkIfIsBlocked(modelAdapeterConnectionsCloudUserInfo: ModelAdapeterConnectionsCloudUserInfo) {
        dataListBlockedUsers.forEach {
            if (it.blockBy.id == sessionManager.user.id && it.blockTo.id == modelAdapeterConnectionsCloudUserInfo.userCloudGallery.id) {
                modelAdapeterConnectionsCloudUserInfo.isBlocked = true
            }
        }
        ThreadUtils.runOnUiThread {
            dataListUsers.add(modelAdapeterConnectionsCloudUserInfo)
            adapterAllUsers.notifyDataSetChanged()
        }

    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): AllUsersFragment {
            val fragment = AllUsersFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }

}