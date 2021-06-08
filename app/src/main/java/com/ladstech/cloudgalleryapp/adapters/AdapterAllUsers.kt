package com.ladstech.cloudgalleryapp.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.bumptech.glide.Glide
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.RowUserBinding
import com.ladstech.cloudgalleryapp.models.ModelAdapeterConnectionsCloudUserInfo
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.Helper
import kotlinx.android.synthetic.main.row_user.view.*


class AdapterAllUsers(
    var mContext: Context,
    var dataList: List<ModelAdapeterConnectionsCloudUserInfo>
) :
    RecyclerView.Adapter<AdapterAllUsers.MyViewHolder>() {

    internal var mOnItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: RowUserBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.btn_follow.setOnClickListener(this)
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
            RowUserBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            with(dataList[position]) {
                binding.tvName.text = userCloudGallery.name
                binding.tvPhone.text = userCloudGallery.phone
                Glide.with(mContext).load(Helper.getImageUrl(userCloudGallery.image))
                    .placeholder(R.drawable.eclipse)
//                    .skipMemoryCache(true)
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(binding.userAvatarIv)


                Log.d("statussss", connectionStatus)
                when (connectionStatus) {
                    "" -> {
                        binding.btnFollow.text = "Connect"
                    }
                    AppConstant.REJECTED -> {
                        binding.btnFollow.text = "Connect"
                    }
                    AppConstant.PENDING -> {
                        binding.btnFollow.text = "Cancel Request"
                    }
                    AppConstant.ACCEPTED -> {
                        binding.btnFollow.text = "View Profile"
                    }


                }


            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

}