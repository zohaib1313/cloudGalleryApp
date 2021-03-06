package com.ladstech.cloudgalleryapp.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.BlockedUsers
import com.amplifyframework.datastore.generated.model.Comments
import com.amplifyframework.datastore.generated.model.Posts
import com.amplifyframework.datastore.generated.model.UserCloudGallery

import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.bumptech.glide.Glide
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.RowBlocekedUsersBinding
import com.ladstech.cloudgalleryapp.databinding.RowCommentsBinding

import com.ladstech.cloudgalleryapp.databinding.RowPostsBinding
import com.ladstech.cloudgalleryapp.utils.Helper

import kotlinx.android.synthetic.main.row_posts.view.*


class AdapterBlockedUsers(
    var mContext: Context,
    var dataList: List<UserCloudGallery>
) :
    RecyclerView.Adapter<AdapterBlockedUsers.MyViewHolder>() {

    internal var mOnItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: RowBlocekedUsersBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)


        }

        override fun onClick(view: View) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener?.onItemClick(
                    view,
                    adapterPosition,
                    ""
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            RowBlocekedUsersBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            with(dataList[position]) {

                binding.tvName.text = name
                if (image != "null") {
                    Glide.with(mContext).load(Helper.getImageUrl(image))
                        .placeholder(R.drawable.eclipse).into(binding.ivUser)

                }

            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

}