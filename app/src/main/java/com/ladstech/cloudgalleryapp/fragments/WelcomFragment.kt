package com.ladstech.cloudgalleryapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.FragmentMyAccountBinding
import com.ladstech.cloudgalleryapp.databinding.FragmentWelcomBinding
import java.lang.Exception

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
    override fun onResume() {
        super.onResume()
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(),R.color.app_color_blue)

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