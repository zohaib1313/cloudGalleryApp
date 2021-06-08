package com.ladstech.cloudgalleryapp.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.solver.widgets.Helper
import com.ladstech.cloudgalleryapp.activities.SignUpActivity
import com.ladstech.cloudgalleryapp.databinding.FragmentMyAccountBinding
import com.ladstech.cloudgalleryapp.databinding.FragmentPublicFeedBinding


class MyAccountFragment : BaseFragment() {

    lateinit var mBinding: FragmentMyAccountBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentMyAccountBinding.inflate(layoutInflater)
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild
        mBinding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            com.ladstech.cloudgalleryapp.utils.Helper.startActivity(
                requireActivity(),
                Intent(requireContext(), SignUpActivity::class.java),
                true
            )
        }
        return mBinding.root
    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): MyAccountFragment {
            val fragment = MyAccountFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }

}