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
import androidx.lifecycle.MutableLiveData
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
//    var dataListPosts = MutableLiveData<Posts>()
//    var dataListLikes = ArrayList<Likes>()
//    var dataListComments = ArrayList<Comments>()
//    var dataListConnections = ArrayList<Connections>()
//    var dataListBlockedUsers = ArrayList<BlockedUsers>()
//    var dataListViews = ArrayList<Views>()
//    var dataListUserBadges = ArrayList<UserBadges>()
//    var dataListBadges = ArrayList<Badges>()


    lateinit var  viewModelComments: CommentsViewModel
    lateinit var viewModelLikes:LikesViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        sessionManager = SessionManager.getInstance(mContext.applicationContext)
        //   isLoggedIn = sessionManager.isLoggedIn

        viewModelComments = ViewModelProviders.of(this@BaseActivity).get(CommentsViewModel::class.java)
        viewModelLikes = ViewModelProviders.of(this@BaseActivity).get(LikesViewModel::class.java)

        setStatusBarMode(true)
        setStatusBarTransparent(this)



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