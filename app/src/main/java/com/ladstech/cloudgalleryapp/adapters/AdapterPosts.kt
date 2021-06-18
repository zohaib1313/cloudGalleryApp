package com.ladstech.cloudgalleryapp.adapters


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Posts

import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.bumptech.glide.Glide
import com.ladstech.cloudgalleryapp.R

import com.ladstech.cloudgalleryapp.databinding.RowPostsBinding
import com.ladstech.cloudgalleryapp.utils.Helper

import kotlinx.android.synthetic.main.row_posts.view.*


class AdapterPosts(
    var mContext: Context,
    var dataList: List<Posts>
) :
    RecyclerView.Adapter<AdapterPosts.MyViewHolder>() {

    internal var mOnItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: RowPostsBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.btnDownloadPost.setOnClickListener(this)
            binding.root.btnMorePost.setOnClickListener(this)
            binding.root.ivUser.setOnClickListener(this)
            binding.root.imageView5.setOnClickListener(this)


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
            RowPostsBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            with(dataList[position]) {
                Glide.with(mContext).load(Helper.getImageUrl(image)).placeholder(R.drawable.eclipse)
                    .into(binding.imageView5)
                Log.d("taaag",image+" ==")
                Log.d("taaag",Helper.getImageUrl(image))
                Glide.with(mContext).load(Helper.getImageUrl(whoPostedUser.image)).placeholder(R.drawable.eclipse)
                    .into(binding.ivUser)
                binding.tvName.text = whoPostedUser.name
                binding.tvTime.text = Helper.getAwsDate(createdTime.toLong())
            }
        }

    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

}