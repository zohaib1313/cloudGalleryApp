package com.ladstech.cloudgalleryapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Connections
import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.adapters.AdapterConnections

import com.ladstech.cloudgalleryapp.databinding.FragmentRequestsBinding
import com.ladstech.cloudgalleryapp.utils.AppConstant


class RequestsFragment : BaseFragment() {

    lateinit var mBinding: FragmentRequestsBinding
    lateinit var adapterConnections: AdapterConnections
    private var dataListRequests = ArrayList<Connections>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentRequestsBinding.inflate(layoutInflater)
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild
        initRv()
        return mBinding.root
    }

    private fun initRv() {
        mBinding.rvRequests.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvRequests.setHasFixedSize(true)
        adapterConnections = AdapterConnections(requireContext(), dataListRequests)
        mBinding.rvRequests.adapter = adapterConnections
        adapterConnections.notifyDataSetChanged()

        adapterConnections.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {
                val modelConnections = dataListRequests[position]
                if (view.id == R.id.btnAccept) {

                    setStatusOfRequest(modelConnections, position, AppConstant.ACCEPTED)
                } else if (view.id == R.id.btnReject) {
                    setStatusOfRequest(modelConnections, position, AppConstant.REJECTED)
                }


            }
        })

        getListOfRequests()


    }

    private fun getListOfRequests() {
        showLoading()
        val requests = ModelQuery.list(Connections::class.java)
        Amplify.API.query(requests, { response ->
            if (response.data != null) {
                response.data.forEach { connections ->
                    if (connections.followTo.id == sessionManager.user.id) {


                        printLog("status ${connections.status}")
                       // if (connections.status != AppConstant.REJECTED || connections.status != AppConstant.ACCEPTED) {
                            dataListRequests.add(connections)
                     //   }
                        ThreadUtils.runOnUiThread {
                            hideLoading()
                            adapterConnections.notifyDataSetChanged()
                        }
                    }

                }
                if (dataListRequests.isEmpty()) {
                    ThreadUtils.runOnUiThread {
                        hideLoading()
                        showNoDataLayout()
                    }
                }

            } else {
                ThreadUtils.runOnUiThread {
                    hideLoading()
                    showNoDataLayout()
                }
            }
        }, {})

    }

    private fun setStatusOfRequest(modelConnections: Connections, position: Int, status: String) {
        showLoading()
        val updatedConnection =
            modelConnections.copyOfBuilder().status(status).build()
        Amplify.API.mutate(ModelMutation.update(updatedConnection), { response ->
            if (response.hasErrors()) {
                ThreadUtils.runOnUiThread {
                    hideLoading()
                    Toast.makeText(
                        requireContext().applicationContext,
                        "failed to $status",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                ThreadUtils.runOnUiThread {
                    hideLoading()
                    dataListRequests[position] =
                        dataListRequests[position].copyOfBuilder().status(status)
                            .build()
                    adapterConnections.notifyDataSetChanged()
                }
            }

        }, {})


    }


    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): RequestsFragment {
            val fragment = RequestsFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }

}