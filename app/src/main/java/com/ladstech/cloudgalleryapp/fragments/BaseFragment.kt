package com.ladstech.cloudgalleryapp.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.ladstech.cloudgalleryapp.utils.AppConstant.Companion.TAG

import com.ladstech.cloudgalleryapp.utils.SessionManager


abstract class BaseFragment : Fragment(), View.OnClickListener {

    open var isAttached = false

    lateinit var sessionManager: SessionManager
    var isLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        sessionManager = SessionManager.getInstance(requireContext().applicationContext)
        //   isLoggedIn = sessionManager.isLoggedIn
    }


    override fun onClick(v: View) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        isAttached = true
    }


    var loadingLayout: RelativeLayout? = null
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

    fun printLog(string: String) {
        Log.d(TAG, string+"\n")
    }


//
//    fun logout(user: User) {
//        sessionManager.clearSession()
//        //api call
//        Helper.startActivity(requireContext() as Activity, Intent(requireContext(), SignInActivity::class.java), true)
//    }


}
