package com.ladstech.cloudgalleryapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.api.graphql.model.ModelSubscription
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.Comments
import com.amplifyframework.datastore.generated.model.Likes
import com.amplifyframework.datastore.generated.model.Posts
import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.ladstech.cloudgalleryapp.R
import com.ladstech.cloudgalleryapp.adapters.AdapterComments
import com.ladstech.cloudgalleryapp.databinding.ActivityPostDetailBinding
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.Helper
import io.supercharge.shimmerlayout.ShimmerLayout


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

        ///get post from intent
        //   showLoading()
        intent?.let {
            post = Gson().fromJson(it.getStringExtra(AppConstant.KEY_DATA), Posts::class.java)

            ///user image
            Glide.with(this@PostDetailActivity).load(Helper.getImageUrl(post.whoPostedUser.image))
                .placeholder(R.drawable.eclipse).into(mBinding.ivUser)
            //post image
            Glide.with(this@PostDetailActivity).load(Helper.getImageUrl(post.image))
                .placeholder(R.drawable.eclipse).into(mBinding.ivPostBg)
            mBinding.tvPostName.text = post.title
            mBinding.tvTime.text = Helper.getAwsDate(post.createdTime.toLong())

            post.let { post ->
                shimmer.startShimmerAnimation()
                initRv()
                getCommentsOfPost(post)
                getLikesOfPost(post)
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
                { printLog("..comment added successfully..") },
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
        Amplify.API.query(
            ModelQuery.list(
                Likes::class.java
            ),
            { response ->
                ThreadUtils.runOnUiThread {
                    shimmer.stopShimmerAnimation()
                }
                response.data.forEach { likes ->
                    if (likes.postId == post.id) {
                        dataListLikes.add(likes)

                    }
                }

                ///total comments
                ThreadUtils.runOnUiThread {
                    mBinding.tvLikesCount.text = dataListLikes.size.toString()
                }
            },
            {
                ThreadUtils.runOnUiThread {
                    shimmer.stopShimmerAnimation()
                }
                printLog("comments query failed ${it.cause}")
            }
        )
        subScribeToLikesCreate(post)
        subScribeToLikesDelete(post)
        checkYourLike()
    }

    private fun subScribeToLikesCreate(post: Posts) {

        Amplify.API.subscribe(ModelSubscription.onCreate(Likes::class.java),
            { establish -> },
            { recieived ->

                val likes = recieived.data as Likes
                if (likes.postId == post.id) {
                    dataListLikes.add(likes)

                    ThreadUtils.runOnUiThread {
                        checkYourLike()
                        mBinding.tvLikesCount.text = dataListLikes.size.toString()
                    }
                }

            },
            { failed -> },
            {//complete})
            })
    }

    private fun subScribeToLikesDelete(post: Posts) {

        Amplify.API.subscribe(ModelSubscription.onDelete(Likes::class.java),
            { establish -> },
            { recieived ->

                val likes = recieived.data as Likes
                if (likes.postId == post.id) {
                    dataListLikes.remove(likes)
                    ThreadUtils.runOnUiThread {
                        checkYourLike()
                        mBinding.tvLikesCount.text = dataListLikes.size.toString()
                    }
                }

            },
            { failed -> },
            {//complete})
            })
    }

    private fun getCommentsOfPost(post: Posts) {
        Amplify.API.query(
            ModelQuery.list(
                Comments::class.java
            ),
            { response ->
                ThreadUtils.runOnUiThread {
                    shimmer.stopShimmerAnimation()
                }

                response.data.forEach { comments ->

                    if (comments.post.id == post.id) {
                        printLog(comments.toString())
                        dataListComments.add(comments)

                    }


                }

                ///total comments
                ThreadUtils.runOnUiThread {
                    adapterComments.notifyDataSetChanged()
                    mBinding.tvCommentsCount.text = dataListComments.size.toString()
                }
            },
            {
                ThreadUtils.runOnUiThread {
                    shimmer.stopShimmerAnimation()
                }
                printLog("comments query failed ${it.cause}")
            }
        )

        subScribeToComments()
    }

    private fun subScribeToComments() {


        Amplify.API.subscribe(ModelSubscription.onCreate(Comments::class.java),
            { establish -> },
            { recieived ->

                val comment = recieived.data as Comments
                if (comment.post.id == post.id) {
                    dataListComments.add(comment)
                    ThreadUtils.runOnUiThread {
                        adapterComments.notifyDataSetChanged()
                        mBinding.tvCommentsCount.text = dataListComments.size.toString()
                    }
                }

            },
            { failed -> },
            {//complete})
            })

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