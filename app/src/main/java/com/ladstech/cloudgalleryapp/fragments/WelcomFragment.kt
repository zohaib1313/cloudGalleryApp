package com.ladstech.cloudgalleryapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.FragmentMyAccountBinding
import com.ladstech.cloudgalleryapp.databinding.FragmentWelcomBinding

class WelcomFragment : BaseFragment() {

    lateinit var mBinding: FragmentWelcomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
          mBinding = FragmentWelcomBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return mBinding.root
    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): WelcomFragment {
            val fragment = WelcomFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }
}