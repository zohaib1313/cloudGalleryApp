package com.ladstech.cloudgalleryapp.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.datastore.generated.model.Comments
import com.amplifyframework.datastore.generated.model.Posts

import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.bumptech.glide.Glide
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.databinding.RowCommentsBinding

import com.ladstech.cloudgalleryapp.databinding.RowPostsBinding
import com.ladstech.cloudgalleryapp.utils.Helper

import kotlinx.android.synthetic.main.row_posts.view.*


class AdapterComments(
    var mContext: Context,
    var dataList: List<Comments>
) :
    RecyclerView.Adapter<AdapterComments.MyViewHolder>() {

    internal var mOnItemClickListener: OnItemClickListener? = null

    inner class MyViewHolder(val binding: RowCommentsBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.likeToggleComment.setOnClickListener(this)

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
            RowCommentsBinding.inflate(LayoutInflater.from(mContext), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        with(holder) {
            with(dataList[position]) {

                binding.tvPersonName.text = whoCommentedUser.name
                binding.tvComment.text = content
                Glide.with(mContext).load(Helper.getImageUrl(whoCommentedUser.image))
                    .placeholder(R.drawable.eclipse).into(binding.ivUser)
                Log.d("taaag", Helper.getImageUrl(whoCommentedUser.image))

            }
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

}