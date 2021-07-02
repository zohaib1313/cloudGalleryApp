package com.ladstech.cloudgalleryapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.GraphQLRequest
import com.amplifyframework.api.graphql.PaginatedResult
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelPagination
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.api.graphql.model.ModelSubscription
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.BlockedUsers
import com.amplifyframework.datastore.generated.model.Connections
import com.amplifyframework.datastore.generated.model.Posts
import com.amplifyframework.datastore.generated.model.UserCloudGallery
import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.adapters.AdapterMyProfilePosts
import com.ladstech.cloudgalleryapp.databinding.ActivityMyProfileBinding

import com.ladstech.cloudgalleryapp.room.entities.PostsTable
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.Helper


class ProfileDetailsWatchActivity : BaseActivity() {

    private lateinit var mBinding: ActivityMyProfileBinding
    private lateinit var adapterMyProfilePost: AdapterMyProfilePosts
    private var dataListPosts = ArrayList<Posts>()
    private var dataListConnections = ArrayList<Connections>()
    private var dataListBlockedUsers = ArrayList<BlockedUsers>()
    private var dataLisFollowers = ArrayList<Connections>()
    private var dataListFollowing = ArrayList<Connections>()
    private var user: UserCloudGallery? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMyProfileBinding.inflate(layoutInflater)

        setContentView(mBinding.root)
        ///user whose profile is being watched
        intent.getStringExtra(AppConstant.KEY_DATA)?.let { userJson ->
            user = Gson().fromJson(userJson, UserCloudGallery::class.java)

            user?.let { user ->
                initRv()

                queryBlockedUser(
                    ModelQuery.list(
                        BlockedUsers::class.java,
                        ModelPagination.limit(1_000)
                    )
                )
                subscribeToBlockedUsers()
                subscribeToConnections()


                //image
                Glide.with(this@ProfileDetailsWatchActivity)
                    .load(Helper.getImageUrl(user.image))
                    .placeholder(R.drawable.eclipse).into(mBinding.circularImageView)
                //name
                mBinding.tvName.text = user.name
                if (sessionManager.user.id == user.id) {
                    mBinding.button6.visibility = View.GONE
                } else {
                    mBinding.button6.visibility = View.VISIBLE
                }
            }
        }


    }

    private fun queryBlockedUser(request: GraphQLRequest<PaginatedResult<BlockedUsers>>) {
        dataListBlockedUsers.clear()
        dataListConnections.clear()
        dataListPosts.clear()
        ThreadUtils.runOnUiThread {
            adapterMyProfilePost.notifyDataSetChanged()
        }
        Amplify.API.query(request,
            { response ->
                if (response.hasData()) {
                    response.data.items.forEach { blockedUsers ->
                        dataListBlockedUsers.add(blockedUsers)
                    }
                    if (response.data.hasNextResult()) {
                        queryBlockedUser(response.data.requestForNextResult)
                    } else {
                        var heHasBlockedMe:BlockedUsers? = null
                        var youHaveBlocked :BlockedUsers? = null
                        printLog("checking if he has blocked me")
                        dataListBlockedUsers.forEach { blockedUser ->
                            if (blockedUser.blockTo.id == sessionManager.user.id && blockedUser.blockBy.id == user!!.id) {
                                heHasBlockedMe = blockedUser
                                return@forEach
                            }
                        }
                        dataListBlockedUsers.forEach { blockedUser ->
                            if (blockedUser.blockBy.id == sessionManager.user.id && blockedUser.blockTo.id == user!!.id) {
                                youHaveBlocked = blockedUser
                                return@forEach
                            }
                        }
                        if (youHaveBlocked!=null) {


                            ThreadUtils.runOnUiThread {
                                mBinding.tvFollowingCount.text = "0"
                                mBinding.tvFollowerCount.text = "0"
                                mBinding.tvPostCount.text = "0"
                                mBinding.btnEditProfile.text = "Blocked"
                            }
                            mBinding.button6.apply {
                                text = "Unblock"
                                setOnClickListener {
                                    Amplify.API.mutate(ModelMutation.delete(youHaveBlocked!!),
                                        {

                                            if (it.hasErrors()) {
                                                printLog(it.errors[0].toString())
                                            } else {
                                                printLog("user unblocked")
                                            }
                                        },
                                        {})
                                }
                            }
                        }
                        else if(heHasBlockedMe!=null){
                            ThreadUtils.runOnUiThread {
                                mBinding.tvFollowingCount.text = "0"
                                mBinding.tvFollowerCount.text = "0"
                                mBinding.tvPostCount.text = "0"
                                mBinding.btnEditProfile.text = "Blocked"
                            }
                             if(youHaveBlocked==null){
                                 val modelBlock = BlockedUsers.Builder()
                                     .blockBy(sessionManager.user)
                                     .blockTo(user)
                                     .build()
                                 mBinding.button6.apply {
                                     text = "Block"
                                     setOnClickListener {
                                         Amplify.API.mutate(ModelMutation.create(modelBlock),
                                             { printLog("user blocked") },
                                             {})
                                     }
                                 }
                             }else{

                                 mBinding.button6.apply {
                                     text = "Unblock"
                                     setOnClickListener {
                                         Amplify.API.mutate(ModelMutation.delete(youHaveBlocked!!),
                                             { printLog("user blocked") },
                                             {})
                                     }
                                 }
                             }
                        }else{
                            val modelBlock = BlockedUsers.Builder()
                                .blockBy(sessionManager.user)
                                .blockTo(user)
                                .build()
                            mBinding.button6.apply {
                                text = "Block"
                                setOnClickListener {
                                    Amplify.API.mutate(ModelMutation.create(modelBlock),
                                        { printLog("user blocked") },
                                        {})
                                }
                            }
                            loadConnections(

                                ModelQuery.list(
                                    Connections::class.java,
                                    ModelPagination.limit(1_000)
                                )
                            )
                        }
                    }
                }
            },
            { Log.e("MyAmplifyApp", "Query failed", it) })


    }

    private fun subscribeToBlockedUsers() {
        ///.....................................................................................create.....................................///
        val subscriptionCreate = Amplify.API.subscribe(
            ModelSubscription.onCreate(BlockedUsers::class.java),
            { },
            {
                Log.i(
                    AppConstant.TAG,
                    "BlockedUsers create subscription received: ${(it.data as BlockedUsers).toString()}"
                )

                queryBlockedUser(
                    ModelQuery.list(
                        BlockedUsers::class.java,
                        ModelPagination.limit(1_000)
                    )
                )
            },
            {},
            {}
        )
        ///.....................................................................................delete.............................................................///
        val subscriptionDelete = Amplify.API.subscribe(
            ModelSubscription.onDelete(BlockedUsers::class.java),
            { },
            {
                Log.i(
                    AppConstant.TAG,
                    " BlockedUser subscriptionDelete received: ${(it.data as BlockedUsers).toString()}"
                )
                queryBlockedUser(
                    ModelQuery.list(
                        BlockedUsers::class.java,
                        ModelPagination.limit(1_000)
                    )
                )

            }, {}, {}
        )

        ///....................................................................................update.....................................///
        val subscriptionUpdate = Amplify.API.subscribe(
            ModelSubscription.onUpdate(BlockedUsers::class.java),
            { },
            {
                Log.i(
                    AppConstant.TAG,
                    "BlockedUsers subscriptionUpdate received: ${(it.data as BlockedUsers).toString()}"
                )
                queryBlockedUser(
                    ModelQuery.list(
                        BlockedUsers::class.java,
                        ModelPagination.limit(1_000)
                    )
                )


            }, {}, {}
        )

    }

    private fun loadConnections(request: GraphQLRequest<PaginatedResult<Connections>>) {
        dataListConnections.clear()
        Amplify.API.query(request, { response ->
            if (response.data != null) {
                dataListConnections.addAll(response.data)
                if (response.data.hasNextResult()) {
                    loadConnections(response.data.requestForNextResult)
                } else {
                    ThreadUtils.runOnUiThread {
                        hideLoading()
                        checkFollowUnfollowCount()
                    }
                }
            } else {
                printLog("no connections")
                ThreadUtils.runOnUiThread {
                    hideLoading()
                    mBinding.tvFollowerCount.text = "0"
                    mBinding.tvFollowingCount.text = "0"
                }
            }
        }, {
            printLog("error getting connections")
            ThreadUtils.runOnUiThread {
                hideLoading()
                mBinding.tvFollowerCount.text = "0"
                mBinding.tvFollowingCount.text = "0"
            }
        })

    }

    private fun checkFollowUnfollowCount() {
        dataListFollowing.clear()
        dataLisFollowers.clear()
        dataListConnections.forEach { connections ->
            if (connections.followBy.id == user!!.id && connections.status == AppConstant.ACCEPTED) {
                //its following
                dataListFollowing.add(connections)
            } else if (connections.followTo.id == user!!.id && connections.status == AppConstant.ACCEPTED) {
                //its followers
                dataLisFollowers.add(connections)
            }
        }


        ThreadUtils.runOnUiThread {
            //inverse logic
            mBinding.tvFollowerCount.text = dataLisFollowers.size.toString()
            mBinding.tvFollowingCount.text = dataListFollowing.size.toString()
        }

        if (user!!.id == sessionManager.user.id) {
            mBinding.btnEditProfile.apply {
                text = "Edit Profile"
                setOnClickListener {
                    //edit profile
                    Helper.startActivity(
                        this@ProfileDetailsWatchActivity,
                        Intent(this@ProfileDetailsWatchActivity, EditProfileActivity::class.java),
                        false
                    )
                }
            }

            loadMyPosts(ModelQuery.list(Posts::class.java, ModelPagination.limit(1_000)))
        } else {

            printLog("checking if already following")
            loadMyPosts(ModelQuery.list(Posts::class.java, ModelPagination.limit(1_000)))
            ///check if i am following it or not
            var isAlreadyFollowingUser: Connections? = null
            dataListConnections.forEach { connections ->
                if (connections.followBy.id == sessionManager.user.id && connections.followTo.id == user!!.id) {
                    //already following
                    isAlreadyFollowingUser = connections
                    return@forEach
                }

            }



            if (isAlreadyFollowingUser == null) {
                printLog("not already following")
                //follow it
                mBinding.btnEditProfile.apply {
                    text = "Follow"
                    setOnClickListener {
                        //follow it
                        val modelConnection = Connections.builder()
                            .status(AppConstant.ACCEPTED)
                            .followBy(sessionManager.user)
                            .followTo(user)
                            .build()
                        Amplify.API.mutate(ModelMutation.create(modelConnection), {}, {})
                    }
                }

                if (user!!.id != sessionManager.user.id && !user!!.isPublic) {
                    printLog("its private account")
                    mBinding.layoutPrivateAccount.visibility = View.VISIBLE
                } else {
                    printLog("its public account")
                    mBinding.layoutPrivateAccount.visibility = View.GONE
                }


            } else {


                printLog("already following")


                when (isAlreadyFollowingUser!!.status) {
                    AppConstant.PENDING -> {
                        mBinding.btnEditProfile.apply {
                            text = "Cancel Request"
                            setOnClickListener {
                                //delete request
                                Amplify.API.mutate(
                                    ModelMutation.delete(
                                        isAlreadyFollowingUser!!.copyOfBuilder().build()
                                    ), {}, {})
                            }
                        }

                        if (user!!.id != sessionManager.user.id && !user!!.isPublic) {
                            mBinding.layoutPrivateAccount.visibility = View.VISIBLE
                        } else {
                            mBinding.layoutPrivateAccount.visibility = View.GONE
                        }
                        printLog("already following status pending")
                    }

                    AppConstant.ACCEPTED -> {
                        mBinding.btnEditProfile.apply {
                            text = "Unfollow"
                            setOnClickListener {
                                //delete request
                                Amplify.API.mutate(
                                    ModelMutation.delete(
                                        isAlreadyFollowingUser!!.copyOfBuilder().build()
                                    ), {}, {})
                            }
                        }
                        printLog("already following status accepted")

                        mBinding.layoutPrivateAccount.visibility = View.GONE
                    }


                    AppConstant.REJECTED -> {
                        printLog("already following status rejected")

                        mBinding.layoutPrivateAccount.visibility = View.VISIBLE
                    }

                }

            }

        }

    }

    private fun subscribeToConnections() {

        ///.....................................................................................create.....................................///
        val subscriptionCreate = Amplify.API.subscribe(
            ModelSubscription.onCreate(Connections::class.java),
            { },
            {
                Log.i(
                    AppConstant.TAG,
                    "connection create subscription received: ${(it.data as Connections).toString()}"
                )

                loadConnections(
                    ModelQuery.list(
                        Connections::class.java,
                        ModelPagination.limit(1_000)
                    )
                )
            },
            {},
            {}
        )
        ///.....................................................................................delete.............................................................///
        val subscriptionDelete = Amplify.API.subscribe(
            ModelSubscription.onDelete(Connections::class.java),
            { },
            {
                Log.i(
                    AppConstant.TAG,
                    "connection onDelete subscription received: ${(it.data as Connections).toString()}"
                )
                loadConnections(
                    ModelQuery.list(
                        Connections::class.java,
                        ModelPagination.limit(1_000)
                    )
                )

            }, {}, {}
        )

        ///....................................................................................update.....................................///
        val subscriptionUpdate = Amplify.API.subscribe(
            ModelSubscription.onUpdate(Connections::class.java),
            { },
            {
                Log.i(
                    AppConstant.TAG,
                    "connection onUpdate subscription received: ${(it.data as Connections).toString()}"
                )
                loadConnections(
                    ModelQuery.list(
                        Connections::class.java,
                        ModelPagination.limit(1_000)
                    )
                )

            }, {}, {}
        )
    }


    private fun loadMyPosts(request: GraphQLRequest<PaginatedResult<Posts>>) {

        dataListPosts.clear()
        Amplify.API.query(request, { response ->
            if (response.data != null) {
                response.data.forEach { posts ->
                    if (posts.whoPostedUser.id == user!!.id) {
                        val postsTable = PostsTable()
                        postsTable.postId = posts.id
                        postsTable.postImage = posts.image

                        postsTable.description = posts.description
                          postsTable.createdTime = posts.createdTime
                        postsTable.whoPostedUser = Gson().toJson(posts.whoPostedUser)
                        dataListPosts.add(posts)
                    }
                }
                if (response.data.hasNextResult()) {
                    loadMyPosts(response.data.requestForNextResult)
                } else {
                    ThreadUtils.runOnUiThread {
                        hideLoading()
                        adapterMyProfilePost.notifyDataSetChanged()
                        mBinding.tvPostCount.text = dataListPosts.size.toString()
                    }
                }

//                initCommentsViewModel()
//                initLikesViewModel()

            } else {
                printLog("no posts")
                ThreadUtils.runOnUiThread { hideLoading() }
            }
        }, {
            printLog("error getting posts")
            ThreadUtils.runOnUiThread { hideLoading() }
        })
    }

    private fun initRv() {

        adapterMyProfilePost =
            AdapterMyProfilePosts(this@ProfileDetailsWatchActivity, dataListPosts)
        mBinding.rvMyPosts.layoutManager = GridLayoutManager(this, 3)
        mBinding.rvMyPosts.setHasFixedSize(true)
        mBinding.rvMyPosts.adapter = adapterMyProfilePost
        adapterMyProfilePost.setOnItemClickListener(object :OnItemClickListener{
            override fun onItemClick(view: View, position: Int, character: String) {

                Helper.startActivity(
                    this@ProfileDetailsWatchActivity,
                    Intent(this@ProfileDetailsWatchActivity, PostDetailActivity::class.java).apply {
                        putExtra(
                            AppConstant.KEY_DATA,
                            Gson().toJson(dataListPosts[position])
                        )
                    },
                    false
                )
            }
        })


        adapterMyProfilePost.notifyDataSetChanged()


    }
}