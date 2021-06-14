package com.ladstech.cloudgalleryapp.activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.callBacks.MessageEvent
import com.ladstech.cloudgalleryapp.databinding.ActivitySplashBinding
import com.ladstech.cloudgalleryapp.fragments.LoginFragment
import com.ladstech.cloudgalleryapp.fragments.ProfileFragmentF
import com.ladstech.cloudgalleryapp.fragments.WelcomFragment
import com.ladstech.cloudgalleryapp.utils.Helper
import com.ladstech.cloudgalleryapp.utils.SessionManager
import kotlinx.android.synthetic.main.activity_splash.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

private const val NUM_PAGES = 3

class SplashActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    lateinit var mBinding: ActivitySplashBinding
    lateinit var fragmentList: ArrayList<Fragment>
    var isEnglish = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        //  this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //  window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        //   window.statusBarColor = Color.TRANSPARENT
        setContentView(mBinding.root)
        sessionManager = SessionManager.getInstance(this.applicationContext)
        mBinding.btnGetStarted.setOnClickListener {

            if (sessionManager.isLoggedIn) {
                Helper.startActivity(
                    this@SplashActivity,
                    Intent(this@SplashActivity, HomeActivity::class.java),
                    true
                )
          finishAffinity()
            } else {
                gotoNextPage()
            }
        }
        mBinding.tvGetStarted.setOnClickListener {
            gotoNextPage()

        }
        if (sessionManager.user != null && sessionManager.isLoggedIn) {
            Helper.sessionRefresh()
        }
        initViewPager()
        EventBus.getDefault().register(this)
    }


    private fun initViewPager() {
        fragmentList = ArrayList()
        fragmentList.add(WelcomFragment.newInstance(0))
        fragmentList.add(LoginFragment.newInstance(0))
        fragmentList.add(ProfileFragmentF.newInstance(0))
        //set pager direction based on language
        if (isEnglish) {
            mBinding.pager.adapter = IntroPagerAdapter(this, fragmentList)
            mBinding.pager.currentItem = 0
        } else {
            fragmentList.reverse()
            mBinding.pager.adapter = IntroPagerAdapter(this, fragmentList)
            mBinding.pager.currentItem = fragmentList.size - 1
        }


        ////swipe to change
        mBinding.pager.isUserInputEnabled = false
        mBinding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                onPageStatChanged()
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                onPageStatChanged()
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
            }
        })


        mBinding.dots.setViewPager2(mBinding.pager)

    }

    private fun onPageStatChanged() {
        when {
            isFirstPage() -> {
                mBinding.btnGetStarted.visibility = View.VISIBLE
                mBinding.tvGetStarted.visibility = View.GONE
                mBinding.btnGetStarted.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.app_color_blue
                    )
                )
                mBinding.dots.selectedDotColor =
                    ContextCompat.getColor(this@SplashActivity, R.color.white)

            }
            isLastPage() -> {

                mBinding.dots.selectedDotColor =
                    ContextCompat.getColor(this@SplashActivity, R.color.white)
                mBinding.btnGetStarted.setColorFilter(
                    ContextCompat.getColor(
                        this,
                        R.color.app_color_blue
                    )
                )
                mBinding.btnGetStarted.visibility = View.GONE
                mBinding.tvGetStarted.visibility = View.VISIBLE
            }
            else -> {
                mBinding.btnGetStarted.visibility = View.VISIBLE
                mBinding.btnGetStarted.setColorFilter(ContextCompat.getColor(this, R.color.white))

                mBinding.tvGetStarted.visibility = View.GONE
                mBinding.dots.selectedDotColor =
                    ContextCompat.getColor(this@SplashActivity, R.color.app_color_yellow)
            }
        }
    }

    private fun gotoNextPage() {
        if (isLastPage()) {
//            val intent = Intent(this@SplashActivity, HomeActivity::class.java)
//            Helper.startActivity(this, intent, false)
            EventBus.getDefault().post(MessageEvent(getString(R.string.login)))
        } else {
            if (isEnglish) {
                mBinding.pager.currentItem += 1
            } else {
                mBinding.pager.currentItem -= 1
            }
        }
    }

    private fun gotoPrevious() {
        if (isEnglish) {
            mBinding.pager.currentItem -= 1
        } else {
            mBinding.pager.currentItem += 1
        }
    }

    private fun isLastPage(): Boolean {
        return if (isEnglish) mBinding.pager.currentItem == (mBinding.pager.adapter?.itemCount!! - 1) else mBinding.pager.currentItem == 0
    }

    private fun isFirstPage(): Boolean {
        return if (isEnglish) mBinding.pager.currentItem == 0 else mBinding.pager.currentItem == (mBinding.pager.adapter?.itemCount!! - 1)
    }

    override fun onBackPressed() {
        if (isFirstPage()) {
            super.onBackPressed()
        } else {
            gotoPrevious()
        }
    }

    private inner class IntroPagerAdapter(fa: FragmentActivity, fragmentList: ArrayList<Fragment>) :
        FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment = fragmentList[position]
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        if (event != null) {
            Log.d("onEventt", event.event)
            when (event.event) {
                getString(R.string.gotonextpage) -> {
                    gotoNextPage()
                }
            }
        }
    }


}
