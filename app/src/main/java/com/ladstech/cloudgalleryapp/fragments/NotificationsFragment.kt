package com.ladstech.cloudgalleryapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ladstech.cloudgalleryapp.databinding.FragmentNotificationsBinding
import com.ladstech.cloudgalleryapp.databinding.FragmentPublicFeedBinding


class NotificationsFragment : BaseFragment() {

    lateinit var mBinding: FragmentNotificationsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentNotificationsBinding.inflate(layoutInflater)
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout=mBinding.noDataLayout.noDataChild
        return mBinding.root
    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): NotificationsFragment {
            val fragment = NotificationsFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }

}