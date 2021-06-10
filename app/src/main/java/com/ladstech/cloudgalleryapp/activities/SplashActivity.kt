package com.ladstech.cloudgalleryapp.activities


import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.ActivitySplashBinding


import com.ladstech.cloudgalleryapp.fragments.LoginFragment
import com.ladstech.cloudgalleryapp.fragments.ProfileFragmentF
import com.ladstech.cloudgalleryapp.fragments.WelcomFragment
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.Helper


import com.ladstech.cloudgalleryapp.utils.SessionManager
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle
import kotlinx.android.synthetic.main.activity_splash.*

private const val NUM_PAGES = 3

class SplashActivity : AppCompatActivity() {
    lateinit var sessionManager: SessionManager
    lateinit var mBinding: ActivitySplashBinding
    lateinit var fragmentList: ArrayList<Fragment>
    var isEnglish = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashBinding.inflate(layoutInflater)
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        window.statusBarColor = Color.TRANSPARENT
        sessionManager = SessionManager.getInstance(this.applicationContext)
        setContentView(mBinding.root)
        mBinding.btnGetStarted.setOnClickListener {

//            if (!sessionManager.isLoggedIn) {
//                val mainIntent = Intent(this, SignUpActivity::class.java)
//                startActivity(mainIntent)
//            } else {
//                val mainIntent = Intent(this, GalleryAppHome::class.java)
//                startActivity(mainIntent)
//            }

            gotoNextPage()
        }

        if (sessionManager.user != null && sessionManager.isLoggedIn) {
            Helper.sessionRefresh()
        }



        initViewPager()


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
        //  mBinding.pager.isUserInputEnabled = false
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

        //Bind the title indicator to the adapter
//        mBinding.indicator.setViewPager(mBinding.pager)
       // mBinding.dots.setViewPager2(mBinding.pager)
        mBinding.dots.apply {
            setSliderColor(ContextCompat.getColor(this@SplashActivity,R.color.grey), ContextCompat.getColor(this@SplashActivity,R.color.white))
            setSliderWidth(resources.getDimension(R.dimen._8sdp))
            setSliderHeight(resources.getDimension(R.dimen._8sdp))
            setSlideMode(IndicatorSlideMode.WORM)
            setIndicatorStyle(IndicatorStyle.CIRCLE)
            setPageSize(mBinding.pager!!.adapter!!.itemCount)
            setupWithViewPager(mBinding.pager)
            notifyDataChanged()
        }
    }

    private fun onPageStatChanged() {
        when {
            isFirstPage() -> {

//                mBinding.dots.selectedDotColor=(ContextCompat.getColor(this, R.color.white))
////                mBinding.dots.setStrokeDotsIndicatorColor(
////                    ContextCompat.getColor(
////                        this,
////                        R.color.white
////                    )
////                )

                    mBinding.btnGetStarted.text=""

                mBinding.dots.apply {
                    setSliderColor(
                        ContextCompat.getColor(this@SplashActivity, R.color.grey),
                        ContextCompat.getColor(this@SplashActivity, R.color.white)
                    )
                }
            }
            isLastPage() -> {
             //   mBinding.dots.selectedDotColor=(ContextCompat.getColor(this, R.color.white))
//                mBinding.dots.setStrokeDotsIndicatorColor(
//                    ContextCompat.getColor(
//                        this,
//                        R.color.white
//                    )
//                )
                mBinding.dots.apply {
                    setSliderColor(
                        ContextCompat.getColor(this@SplashActivity, R.color.grey),
                        ContextCompat.getColor(this@SplashActivity, R.color.white)
                    )
                }
                mBinding.btnGetStarted.text="Let's Start"
            }
            else -> {
//                mBinding.dots.selectedDotColor=(
//                    ContextCompat.getColor(
//                        this,
//                        R.color.app_color_blue
//                    )
//                )
//                mBinding.dots.setStrokeDotsIndicatorColor(
//                    ContextCompat.getColor(
//                        this,
//                        R.color.app_color_blue
//                    )
//                )
                mBinding.btnGetStarted.text="Next"
                mBinding.dots.apply {
                    setSliderColor(
                        ContextCompat.getColor(this@SplashActivity, R.color.grey),
                        ContextCompat.getColor(this@SplashActivity, R.color.app_color_blue)
                    )
                }

            }
        }
    }

    private fun gotoNextPage() {
        if (isLastPage()) {


            val intent = Intent(this@SplashActivity, HomeActivity::class.java)
            Helper.startActivity(this, intent, false)

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

}
