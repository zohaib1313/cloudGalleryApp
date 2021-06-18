package com.ladstech.cloudgalleryapp.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelPagination
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.api.graphql.model.ModelSubscription
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.*
import com.google.gson.Gson
import com.jaeger.library.StatusBarUtil
import com.ladstech.cloudgalleryapp.room.entities.CommentsTable
import com.ladstech.cloudgalleryapp.room.entities.LikesTable
import com.ladstech.cloudgalleryapp.room.entities.PostsTable
import com.ladstech.cloudgalleryapp.room.viewModels.CommentsViewModel
import com.ladstech.cloudgalleryapp.room.viewModels.LikesViewModel
import com.ladstech.cloudgalleryapp.room.viewModels.PostsViewModel
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.NetworkChangeReceiver
import com.ladstech.cloudgalleryapp.utils.SessionManager


open class BaseActivity : AppCompatActivity() {
    var isLoggedIn = false
    lateinit var sessionManager: SessionManager
    lateinit var mContext: Context
    open val TAG: String = AppConstant.TAG
    var isFromNotificationsBackPress = false
    private val mNetworkReceiver: BroadcastReceiver = NetworkChangeReceiver()
    //  var backStackC


    //dataLists
//    var dataListPosts = ArrayList<PostsTable>()
//    var dataListLikes = ArrayList<Likes>()
//    var dataListComments = ArrayList<Comments>()
//    var dataListConnections = ArrayList<Connections>()
//    var dataListBlockedUsers = ArrayList<BlockedUsers>()
//    var dataListViews = ArrayList<Views>()
//    var dataListUserBadges = ArrayList<UserBadges>()
//    var dataListBadges = ArrayList<Badges>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        sessionManager = SessionManager.getInstance(mContext.applicationContext)
        //   isLoggedIn = sessionManager.isLoggedIn
        setStatusBarMode(true)
        setStatusBarTransparent(this)
        registerNetworkBroadcastForNougat()
        initPostsViewModel()


    }

    private fun initLikesViewModel() {
        val viewModelLikes =
            ViewModelProviders.of(this@BaseActivity).get(LikesViewModel::class.java)
        val request =
            ModelQuery.list(Likes::class.java)
        Amplify.API.query(request, { response ->
            if (response.data != null) {
                response.data.forEach { likes ->
                    val likesTable = LikesTable()
                    likesTable.id = likes.id
                    likesTable.postId = likes.postId
                    likesTable.whoCommentedUser = Gson().toJson(likes.whoLikedUser)
                    viewModelLikes.insert(likesTable)
                }
            } else {
                printLog("like response is null")
            }
        }, {
            printLog("like exception  ${it.cause}")
        })

        val subscription = Amplify.API.subscribe(
            ModelSubscription.onCreate(Likes::class.java),
            { },
            {
                Log.i(
                    AppConstant.TAG,
                    "Post like subscription received: ${(it.data as Likes)}"
                )
                (it.data as Likes).let { likes ->
                    ThreadUtils.runOnUiThread {
                        val likeTable = LikesTable()
                        likeTable.id = likes.id
                        likeTable.whoCommentedUser = Gson().toJson(likes.whoLikedUser)
                        likeTable.postId = likes.postId
                        viewModelLikes.insert(likeTable)
                    }
                }

            },
            {
                Log.e(AppConstant.TAG, "Subscription failed", it)
//                ThreadUtils.runOnUiThread {
//                    hideLoading()
//                }
            },
            {
                Log.i(AppConstant.TAG, "Subscription completed")
//                ThreadUtils.runOnUiThread {
//                    hideLoading()
//                }
            }
        )


    }

    private fun initCommentsViewModel() {
        val viewModelComments =
            ViewModelProviders.of(this@BaseActivity).get(CommentsViewModel::class.java)
        val request =
            ModelQuery.list(Comments::class.java)
        Amplify.API.query(request, { response ->
            if (response.data != null) {
                response.data.forEach { comments ->
                    val commentTable = CommentsTable()
                    commentTable.id = comments.id
                    commentTable.content = comments.content
                    commentTable.createdTime = comments.createdTime
                    commentTable.post = Gson().toJson(comments.post)
                    commentTable.whoCommentedUser = Gson().toJson(commentTable.whoCommentedUser)
                    viewModelComments.insert(commentTable)
                }
            } else {
                ThreadUtils.runOnUiThread { hideLoading() }
            }
        }, {
            ThreadUtils.runOnUiThread { hideLoading() }
        })

        val subscription = Amplify.API.subscribe(
            ModelSubscription.onCreate(Comments::class.java),
            { },
            {
                Log.i(
                    AppConstant.TAG,
                    "Post create subscription received: ${(it.data as Comments).content}"
                )
                (it.data as Comments).let { comments ->
                    ThreadUtils.runOnUiThread {
                        val commentTable = CommentsTable()
                        commentTable.id = comments.id
                        commentTable.content = comments.content
                        commentTable.createdTime = comments.createdTime
                        commentTable.post = Gson().toJson(comments.post)
                        commentTable.whoCommentedUser = Gson().toJson(commentTable.whoCommentedUser)
                        viewModelComments.insert(commentTable)
                    }
                }
            },
            {
                Log.e(AppConstant.TAG, "Subscription failed", it)
//                ThreadUtils.runOnUiThread {
//                    hideLoading()
//                }
            },
            {
                Log.i(AppConstant.TAG, "Subscription completed")
//                ThreadUtils.runOnUiThread {
//                    hideLoading()
//                }
            }
        )


    }

    private fun initPostsViewModel() {
        showLoading()
        val viewModelPosts =
            ViewModelProviders.of(this@BaseActivity).get(PostsViewModel::class.java)
        val request =
            ModelQuery.list(Posts::class.java)
        Amplify.API.query(request, { response ->
            if (response.data != null) {
                response.data.forEach { posts ->
                    if (posts.isPublic) {
                        val postsTable = PostsTable()
                        postsTable.postId = posts.id
                        postsTable.postImage=posts.image
                        postsTable.title = posts.title
                        postsTable.description = posts.description
                        postsTable.isPublic = posts.isPublic
                        postsTable.createdTime = posts.createdTime
                        postsTable.whoPostedUser = Gson().toJson(posts.whoPostedUser)
                        viewModelPosts?.insert(postsTable)
                    }
                }
//                initCommentsViewModel()
//                initLikesViewModel()
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }

            } else {
                printLog("no posts")
                ThreadUtils.runOnUiThread { hideLoading() }
            }
        }, {
            printLog("error getting posts")
            ThreadUtils.runOnUiThread { hideLoading() }
        })
//!!!!!!!!!!!!!!!  subscriber create !!!!!!!!!!!!!!!!/
        val subscription = Amplify.API.subscribe(
            ModelSubscription.onCreate(Posts::class.java),
            { Log.i(AppConstant.TAG, "post create Subscription established") },
            {
                Log.i(
                    AppConstant.TAG,
                    "Post create subscription received: ${(it.data as Posts).toString()}"
                )
                ThreadUtils.runOnUiThread {
                    if (it.data.isPublic) {
                        val posts = it.data
                        //  dataListPosts.add(it.data as Posts)
                        val postsTable = PostsTable()
                        postsTable.postId = posts.id
                        postsTable.title = posts.title
                        postsTable.description = posts.description
                        postsTable.isPublic = posts.isPublic
                        postsTable.createdTime = posts.createdTime
                        postsTable.whoPostedUser = Gson().toJson(posts.whoPostedUser)
                        viewModelPosts?.insert(postsTable)
                        hideLoading()
                    }
                }
            },
            {
                Log.e(AppConstant.TAG, "Subscription failed", it)
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }
            },
            {
                Log.i(AppConstant.TAG, "Post create Subscription completed")
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }
            }
        )

//!!!!!!!!!!!!!!!  subscriber delete !!!!!!!!!!!!!!!!/


        val subscriptionDelete = Amplify.API.subscribe(
            ModelSubscription.onDelete(Posts::class.java),
            { Log.i(AppConstant.TAG, "post delete Subscription established") },
            {
                Log.i(
                    AppConstant.TAG,
                    "Post delete subscription received: ${(it.data as Posts).title}"
                )
                ThreadUtils.runOnUiThread {
                    if (it.data.isPublic) {
                        val posts = it.data
                        //  dataListPosts.add(it.data as Posts)
                        val postsTable = PostsTable()
                        postsTable.postId = posts.id
                        postsTable.title = posts.title
                        postsTable.description = posts.description
                        postsTable.isPublic = posts.isPublic
                        postsTable.createdTime = posts.createdTime
                        postsTable.whoPostedUser = Gson().toJson(posts.whoPostedUser)
                        viewModelPosts?.delete(postsTable)
                        hideLoading()
                    }
                }
            },
            {
                Log.e(AppConstant.TAG, " Post delete Subscription failed", it)
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }
            },
            {
                Log.i(AppConstant.TAG, "Subscription completed")
                ThreadUtils.runOnUiThread {
                    hideLoading()
                }
            }
        )
        if (subscription != null) {
        printLog("delete subscription started")
            subscription.start()
        }
        if (subscriptionDelete != null) {
            subscriptionDelete.start()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkChanges()
    }

    var loadingLayout: RelativeLayout? = null
    open fun registerNetworkBroadcastForNougat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(
                mNetworkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    protected open fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }


    fun showLoading() {
        loadingLayout?.visibility = View.VISIBLE
    }

    fun hideLoading() {
        loadingLayout?.visibility = View.GONE
    }

    var noDataFoundLayout: RelativeLayout? = null
    fun showNoDataLayout() {
        noDataFoundLayout?.visibility = View.VISIBLE
    }

    fun hideNoDataLayout() {
        noDataFoundLayout?.visibility = View.GONE
    }

    open fun setStatusBarMode(enableDarkMode: Boolean) {
        if (enableDarkMode) {
            StatusBarUtil.setDarkMode(this)
        } else {
            StatusBarUtil.setLightMode(this)
        }
    }


    open fun setStatusBarTransparent(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            activity.window.statusBarColor = Color.WHITE
        } else {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }


    fun printLog(string: String) {
        Log.d(TAG, string + "\n")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE) {
            Amplify.Auth.handleWebUISignInResponse(data)
        }
    }
}