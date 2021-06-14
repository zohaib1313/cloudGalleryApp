package com.ladstech.cloudgalleryapp.activities

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.ActivitySignUpBinding

import com.ladstech.cloudgalleryapp.fragments.FragmentLoginRegisterHammad


class SignUpActivity : BaseActivity() {

    lateinit var mBinding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        changeFragment(FragmentLoginRegisterHammad(), false)
    }


    private fun changeFragment(fragment: Fragment, needToAddBackstack: Boolean) {
        val mFragmentTransaction: FragmentTransaction =
            this.supportFragmentManager.beginTransaction()
        mFragmentTransaction.replace(R.id.fragmentContainerLogin, fragment)
        mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (needToAddBackstack) mFragmentTransaction.addToBackStack(null)
        mFragmentTransaction.commit()
    }

}