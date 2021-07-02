package com.ladstech.cloudgalleryapp.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.api.graphql.model.ModelSubscription
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Comments
import com.amplifyframework.datastore.generated.model.Likes
import com.amplifyframework.datastore.generated.model.Posts
import com.amplifyframework.datastore.generated.model.UserCloudGallery
import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.adapters.AdapterComments
import com.ladstech.cloudgalleryapp.databinding.ActivityPostDetailBinding
import com.ladstech.cloudgalleryapp.room.database.AppDatabase
import com.ladstech.cloudgalleryapp.room.entities.CommentsTable
import com.ladstech.cloudgalleryapp.room.entities.LikesTable
import com.ladstech.cloudgalleryapp.room.entities.PostsTable
import com.ladstech.cloudgalleryapp.room.repositories.CommentsRepository
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.Helper
import io.supercharge.shimmerlayout.ShimmerLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class PostDetailActivity : BaseActivity() {

    private lateinit var mBinding: ActivityPostDetailBinding
    private lateinit var adapterComments: AdapterComments
    private var dataListComments = ArrayList<Comments>()
    private var dataListLikes = ArrayList<Likes>()
    private lateinit var post: Posts

    private lateinit var shimmer: ShimmerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild
        shimmer = mBinding.shimmer
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }
        ///get post from intent
        //   showLoading()
        intent?.let {
            post = Gson().fromJson(it.getStringExtra(AppConstant.KEY_DATA), Posts::class.java)

            ///user image
            Glide.with(this@PostDetailActivity).load(Helper.getImageUrl(sessionManager.user.image))
                .placeholder(R.drawable.eclipse).into(mBinding.ivUserr)
            printLog("user image ${Helper.getImageUrl(sessionManager.user.image)}")

            //post image
            Glide.with(this@PostDetailActivity).load(Helper.getImageUrl(post.image))
                .placeholder(R.drawable.eclipse).into(mBinding.ivPostBg)
            mBinding.tvPostName.text = post.description
            mBinding.tvTime.text = Helper.getAwsDate(post.createdTime.toLong())

            post.let { post ->
                shimmer.startShimmerAnimation()
                shimmer.visibility = View.VISIBLE
                initRv()
                getCommentsOfPost(post)

            }
        }



        mBinding.addComment.setOnClickListener {
            if (mBinding.etComment.text.isEmpty()) {
                Toast.makeText(this, "Enter Comment to Post", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            Helper.hideKeyboard(this)

            val commentModel = Comments.Builder()

                .createdTime(Helper.getCurrentTimeStamp())
                .content(mBinding.etComment.text.toString().trim())
                .post(post)
                .whoCommentedUser(sessionManager.user)
                .build()
            mBinding.etComment.setText("")
            Amplify.API.mutate(ModelMutation.create(commentModel),
                {

                    if (it.hasErrors()) {
                        printLog("..comment added failed ${it.errors[0]}..")
                    } else {
                        printLog("..comment added successfully..")
                    }
                },
                { printLog("..comment added failed..") })


        }

        mBinding.ivLikes.setOnClickListener {
            likePost(post)
        }
    }

    private fun checkYourLike() {
        var isFound = false
        dataListLikes.forEach { likes ->
            if (likes.postId == post.id && likes.whoLikedUser.id == sessionManager.user.id) {
                //do unlike
                isFound = true
                return@forEach
            }
        }
        if (isFound) {
            mBinding.ivLikes.setImageResource(R.drawable.like)
        } else {
            mBinding.ivLikes.setImageResource(R.drawable.unlike)
        }


    }

    private fun likePost(post: Posts) {
        var isFound = false
        var likesModel = Likes.Builder()
            .postId(post.id)
            .whoLikedUser(sessionManager.user)
            .build()

        dataListLikes.forEach { likes ->
            if (likes.postId == post.id && likes.whoLikedUser.id == sessionManager.user.id) {
                //do unlike
                isFound = true
            }
            if (isFound) {
                likesModel = likes.copyOfBuilder()
                    .postId(likes.postId)
                    .whoLikedUser(likes.whoLikedUser)
                    .id(likes.id)
                    .build()
            }

        }
        if (isFound) {
            //unlike it
            Amplify.API.mutate(ModelMutation.delete(likesModel),
                {
                    printLog("..like deleted successfully..")
                    checkYourLike()
                },
                { printLog("..like delete failed..") })

        } else {
            //like it
            Amplify.API.mutate(ModelMutation.create(likesModel),
                {
                    printLog("..like created successfully..")
                    checkYourLike()
                },
                { printLog("..like created failed..") })
        }


    }

    private fun getLikesOfPost(post: Posts) {
        viewModelLikes.let { likes ->

            val observer = Observer<List<LikesTable>>() {
                dataListLikes.clear()
                it.forEach { likesTable ->

                    if (likesTable.postId == post.id)
                        dataListLikes.add(LikesTable.likeObjFromTable(likesTable))
                }
///total comments
                ThreadUtils.runOnUiThread {
                    if (dataListLikes.isEmpty()) {
                        mBinding.tvLikesCount.text = "0"

                    } else {
                        mBinding.tvLikesCount.text = dataListLikes.size.toString()

                    }

                }
                checkYourLike()
            }

            viewModelLikes.liveDataList.observe(this@PostDetailActivity, observer)
        }


    }


    private fun getCommentsOfPost(post: Posts) {


        viewModelComments.let { commentsViewModel ->
            val observer = Observer<List<CommentsTable>>() {
                dataListComments.clear()

                it.forEach { commentsTable ->

                    //      printLog(commentsTable.toString())

                    val comment = CommentsTable.commentObjFromCommentTable(commentsTable)
                    if (comment.post.id == post.id) {
                        printLog(comment.toString())
                        dataListComments.add(comment)
                    }
                }
///total comments
                ThreadUtils.runOnUiThread {
                    adapterComments.notifyDataSetChanged()
                    shimmer.stopShimmerAnimation()
                    shimmer.visibility = View.GONE
                    mBinding.tvCommentsCount.text = dataListComments.size.toString()
                }
            }

            commentsViewModel.allItemsLiveData.observe(this@PostDetailActivity, observer)
        }

        getLikesOfPost(post)


    }


    override fun onDestroy() {
        super.onDestroy()

    }

    private fun initRv() {
        adapterComments = AdapterComments(this@PostDetailActivity, dataListComments)
        mBinding.rvComments.layoutManager = LinearLayoutManager(this)
        mBinding.rvComments.setHasFixedSize(true)
        mBinding.rvComments.adapter = adapterComments
        adapterComments.notifyDataSetChanged()
        adapterComments.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {

                if (view.id == R.id.likeToggleComment) {

                    likeComment(dataListComments[position])
                }
            }

        })
    }

    private fun likeComment(comment: Comments) {

    }
}