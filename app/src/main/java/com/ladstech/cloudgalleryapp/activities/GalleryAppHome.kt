package com.ladstech.cloudgalleryapp.activities

import android.app.AlertDialog
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.ActivityGalleryAppHomeBinding
import com.ladstech.cloudgalleryapp.fragments.*
import com.ladstech.cloudgalleryapp.utils.Helper

class GalleryAppHome : BaseActivity() {
    private var mFragmentManager: FragmentManager? = null
    lateinit var mBinding: ActivityGalleryAppHomeBinding
    lateinit var bottomNavView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityGalleryAppHomeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mFragmentManager = supportFragmentManager
        bottomNavView = mBinding.bottomNavigationView
        bottomNavView.setOnNavigationItemSelectedListener { onBottomNavClick(it) }
        changeFragment(PublicFeedFragment.newInstance(0), false)

        if (sessionManager.user != null) {

            Helper.sessionRefresh()
            Helper.refreshFcmToken(this@GalleryAppHome)

        }

        bottomNavView.menu.forEach { item ->
            TooltipCompat.setTooltipText(findViewById(item.itemId), null)
        }


    }

    var activeTabId = 0
    private fun onBottomNavClick(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.actionPublicFeed -> {
                if (activeTabId != R.id.actionPublicFeed) {
                    changeFragment(PublicFeedFragment.newInstance(0), false)
                }
            }
            R.id.actionPrivateFeed -> {
                if (activeTabId != R.id.actionPrivateFeed) {
                    changeFragment(AllUsersFragment.newInstance(0), false)
                }

            }
            R.id.actionRequests -> {
                if (activeTabId != R.id.actionRequests) {
                    changeFragment(RequestsFragment.newInstance(0), false)

                }
            }
            R.id.actionNotifications -> {
                if (activeTabId != R.id.actionNotifications) {
                    changeFragment(NotificationsFragment.newInstance(0), false)
                }
            }
            R.id.actionMyAccount -> {
                if (activeTabId != R.id.actionMyAccount) {
                    changeFragment(MyAccountFragment.newInstance(0), false)
                }
            }
        }

        activeTabId = item.itemId
        return true
    }


    override fun onDestroy() {
        super.onDestroy()

    }

    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.mainActivityFragmentContainer, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }


    override fun onBackPressed() {

        printLog(supportFragmentManager.backStackEntryCount.toString())

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            printLog("alert dialog")
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit")
                .setCancelable(false)
                .setPositiveButton(
                    "Yes"
                ) { dialog, which ->
                   // finishAffinity()
                    finish()
                    dialog.dismiss()
                }
                .setNegativeButton(
                    "No"
                ) { dialog, which ->
                    dialog.dismiss()
                }
                .create()

            alertDialog.show()
        }

    }


}