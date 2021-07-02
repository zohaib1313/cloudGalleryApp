package com.ladstech.cloudgalleryapp.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Posts

import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.bumptech.glide.Glide
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.RowMyPostsBinding

import com.ladstech.cloudgalleryapp.databinding.RowPostsBinding
import com.ladstech.cloudgalleryapp.utils.Helper

import kotlinx.android.synthetic.main.row_posts.view.*


class AdapterMyProfilePosts(
    var mContext: Context,
    var dataList: List<Posts>
) :
    RecyclerView.Adapter<AdapterMyProfilePosts.MyViewHolder>() {

    internal var mOnItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: RowMyPostsBinding) :
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
            RowMyPostsBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            with(dataList[position]) {
                Glide.with(mContext).load(Helper.getImageUrl(image)).placeholder(R.color.grey)
                    .into(binding.ivPost)

            }
        }

    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

}