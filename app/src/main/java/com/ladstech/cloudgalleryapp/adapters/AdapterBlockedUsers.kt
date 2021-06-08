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

import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.ladstech.cloudgalleryapp.databinding.RowBlocekedUsersBinding
import com.ladstech.cloudgalleryapp.databinding.RowCommentsBinding

import com.ladstech.cloudgalleryapp.databinding.RowPostsBinding

import kotlinx.android.synthetic.main.row_posts.view.*



class AdapterBlockedUsers(
    var mContext: Context,
    var dataList: List<BlockedUsers>
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
        return 10
    }

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
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = onItemClickListener
    }

}