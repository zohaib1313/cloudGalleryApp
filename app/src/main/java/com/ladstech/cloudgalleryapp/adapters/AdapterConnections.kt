package com.ladstech.cloudgalleryapp.adapters


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Connections

import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.ladstech.cloudgalleryapp.databinding.RowConnectionsBinding
import com.ladstech.cloudgalleryapp.utils.AppConstant


class AdapterConnections(
    var mContext: Context,
    var dataList: List<Connections>
) :
    RecyclerView.Adapter<AdapterConnections.MyViewHolder>() {

    internal var mOnItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: RowConnectionsBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.btnAccept.setOnClickListener(this)
            binding.btnReject.setOnClickListener(this)

        }

        override fun onClick(view: View) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener?.onItemClick(
                    view,
                    adapterPosition,
                    dataList[adapterPosition].toString()
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            RowConnectionsBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            with(dataList[position]) {
                binding.tvName.text = followBy.name

                if(status==AppConstant.ACCEPTED){
                    binding.lnrActions.visibility=View.GONE
                    binding.lnrStatus.visibility=View.VISIBLE
                    binding.tvStatus.text = AppConstant.ACCEPTED

                }else if(status==AppConstant.REJECTED){
                    binding.lnrActions.visibility=View.GONE
                    binding.lnrStatus.visibility=View.VISIBLE
                    binding.tvStatus.text = AppConstant.REJECTED
                }else{
                    binding.lnrStatus.visibility=View.GONE
                    binding.lnrActions.visibility=View.VISIBLE
                }

            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

}