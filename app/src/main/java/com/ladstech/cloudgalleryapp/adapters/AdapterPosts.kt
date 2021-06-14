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
import com.ladstech.cloudgalleryapp.R

import com.ladstech.cloudgalleryapp.databinding.RowPostsBinding

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
        return 10
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        with(holder) {
//            with(dataList[position]) {
//
//                Log.d("taaag", likes.size.toString())
//                try {
//
//
//                    post?.let { post ->
//                        binding.tvName.text = post.whoPostedUser.name
////                        binding.tv.text = post.title
////                        binding.pDescriptionTv.text = post.description
//                        binding.tvTime.text = Helper.getAwsDate(post.createdTime.toLong())
//                        Glide.with(mContext).load(Helper.getImageUrl(post.image))
//                            .placeholder(R.drawable.eclipse)
//                            .into(binding.imageView5)
//                        Glide.with(mContext).load(Helper.getImageUrl(post.whoPostedUser.image))
//                            .placeholder(R.drawable.eclipse)
//                            .into(binding.ivUser)
//                    }
//
//
////                    binding.pCommentsTv.text = ("${comments.size.toString()} Comments")
////                    binding.pLikesTv.text = ("${likes.size.toString()} likes")
////                    if (haveILiked) {
////                        binding.likeBtn.text = "Unlike"
////                    }else{
////                        binding.likeBtn.text = "like"
////                    }
//
//                } catch (e: Exception) {
//                }
//
//
//                //                binding.tvName.text = name
////                binding.tvPhone.text=phone
////                binding.tvDateTime.text = Helper.getAwsDate(sharingWithCloudModel.fileTime.toLong())
////                binding.tvSize.text = "${size.toString()} B"
////                sharingWithCloudModel.user.image?.let {
////                    Glide.with(mContext).load(Helper.getImageUrl(it.toString()))
////                        .placeholder(R.drawable.eclipse)
//////                    .skipMemoryCache(true)
//////                    .diskCacheStrategy(DiskCacheStrategy.NONE)
////                        .into(binding.ivContact)
////                }
//
//
//            }
//        }


//        holder!!.binding.btnMorePost!!.setOnClickListener {
//            val popup = PopupMenu(mContext, holder.binding.btnMorePost)
//            popup.inflate(R.menu.menu_f)
//            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
//                override fun onMenuItemClick(p0: MenuItem?): Boolean {
//                    Log.e(">>",p0.toString())
//                    return true
//                }
//
//            })
//            popup.show();
//        }


    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

}