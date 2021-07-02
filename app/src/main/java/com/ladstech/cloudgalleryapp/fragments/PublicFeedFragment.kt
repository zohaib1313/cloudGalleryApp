package com.ladstech.cloudgalleryapp.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

import androidx.recyclerview.widget.LinearLayoutManager
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.api.graphql.model.ModelSubscription
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.generated.model.*
import com.appseen.contacts.sharing.app.callBacks.OnItemClickListener
import com.google.gson.Gson
import com.ladstech.cloudgalleryapp.R

import com.ladstech.cloudgalleryapp.activities.AddPostActivity
import com.ladstech.cloudgalleryapp.adapters.AdapterHomePosts
import com.ladstech.cloudgalleryapp.callBacks.MessageEvent
import com.ladstech.cloudgalleryapp.databinding.FragmentPublicFeedBinding
import com.ladstech.cloudgalleryapp.models.ModelAdapterAllPosts
import com.ladstech.cloudgalleryapp.room.entities.CommentsTable
import com.ladstech.cloudgalleryapp.room.entities.LikesTable
import com.ladstech.cloudgalleryapp.room.entities.PostsTable
import com.ladstech.cloudgalleryapp.room.viewModels.CommentsViewModel
import com.ladstech.cloudgalleryapp.room.viewModels.LikesViewModel
import com.ladstech.cloudgalleryapp.room.viewModels.PostsViewModel

import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.AppConstant.Companion.TAG
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class PublicFeedFragment : BaseFragment() {

    private lateinit var mBinding: FragmentPublicFeedBinding
    private lateinit var adapterHomePosts: AdapterHomePosts
    private var dataListPosts = ArrayList<Posts>()
    var dataListLikes = ArrayList<Likes>()
    var dataListComments = ArrayList<Comments>()
    var dataListAdapterItems = ArrayList<ModelAdapterAllPosts>()
    private lateinit var likesViewModel: LikesViewModel
    override fun onPause() {
        super.onPause()
        EventBus.getDefault().register(this)
    }

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().unregister(this)

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public fun onMessageEvent(event: MessageEvent) {
        printLog("eventttt")
        event.let {
            printLog(it.event + " post fragment")
            if (it.event == AppConstant.POSTCREATED) {
                // getListOfPosts()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentPublicFeedBinding.inflate(layoutInflater)
        loadingLayout = mBinding.loadingLayout.rlLoading
        noDataFoundLayout = mBinding.noDataLayout.noDataChild
     //   initRv()
        mBinding.fabAddPost.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Chose Post type")
                .setCancelable(true)
                .setPositiveButton("Public Post", DialogInterface.OnClickListener { dialog, which ->

                    com.ladstech.cloudgalleryapp.utils.Helper.startActivity(
                        requireActivity(),
                        Intent(requireContext(), AddPostActivity::class.java).apply {
                            putExtra(AppConstant.KEY_DATA, true)
                        },
                        false
                    )
                    dialog.dismiss()
                })
                .setNegativeButton(
                    "Private Post",
                    DialogInterface.OnClickListener { dialog, which ->
                        com.ladstech.cloudgalleryapp.utils.Helper.startActivity(
                            requireActivity(),
                            Intent(requireContext(), AddPostActivity::class.java).apply {
                                putExtra(AppConstant.KEY_DATA, false)
                            },
                            false
                        )
                        dialog.dismiss()
                    })
                .create()

            dialog.show()


        }
        return mBinding.root
    }


    private fun initRv() {
        mBinding.rvPost.layoutManager = LinearLayoutManager(requireContext())
        mBinding.rvPost.setHasFixedSize(true)
      //  adapterPosts = AdapterPosts(requireContext(), dataListAdapterItems)
        mBinding.rvPost.adapter = adapterHomePosts

        adapterHomePosts.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(view: View, position: Int, character: String) {
                if (view.id == R.id.likeBtn) {
                    val item = dataListAdapterItems[position]
                    val itemUpdated = dataListAdapterItems[position]
                        .apply { haveILiked = (!item.haveILiked) }

                    dataListAdapterItems[position] = itemUpdated
                    adapterHomePosts.notifyDataSetChanged()
                    printLog("sending linking post of ${itemUpdated.haveILiked}")
                    sendLikeRequest(!itemUpdated.haveILiked, dataListAdapterItems[position])


                }


            }
        })

        //getListOfPosts()

        showLoading()
        initPostsObserver()


    }

    private fun sendLikeRequest(haveILiked: Boolean, modelAdapterAllPosts: ModelAdapterAllPosts) {

        val likeBuilder = Likes.builder()
            .postId(modelAdapterAllPosts.post?.id)
            .whoLikedUser(sessionManager.user)

        if (haveILiked) {
            modelAdapterAllPosts.likes.forEach { likes ->
                if (likes.whoLikedUser.id == sessionManager.user.id && likes.postId == modelAdapterAllPosts.post?.id) {
                    //i have liked it delete it
                    likeBuilder.id(likes.id)
                    return@forEach
                }
            }
        }

        if (haveILiked) {
            //deleteing
            likesViewModel.delete(likeBuilder.build().id)
            Amplify.API.mutate(ModelMutation.delete(likeBuilder.build()), {}, {})
        } else {
            Amplify.API.mutate(ModelMutation.create(likeBuilder.build()), {}, {})

        }

    }

    private fun getPostsLikesComments(post: Posts) {

        hideLoading()

        val adapterItemModel = ModelAdapterAllPosts()
        adapterItemModel.post = post
        dataListLikes.forEach {
            if (it.postId == post.id) {
                adapterItemModel.likes.add(it)
                if (it.whoLikedUser.id == sessionManager.user.id) {
                    adapterItemModel.haveILiked = true
                }
            }
        }
        dataListComments.forEach {
            if (it.post.id == post.id)
                adapterItemModel.comments.add(it)
        }
        dataListAdapterItems.add(adapterItemModel)
        adapterHomePosts.notifyDataSetChanged()


    }


    private fun initLikesObserver() {
        likesViewModel = ViewModelProviders.of(requireActivity()).get(LikesViewModel::class.java)
        val observer = Observer<List<LikesTable>>() {
            printLog("liked observer")
            dataListLikes.clear()
            it.forEach { likes ->
                val like = Likes.Builder()
                    .postId(likes.postId)
                    .whoLikedUser(
                        Gson().fromJson(
                            likes.whoCommentedUser,
                            UserCloudGallery::class.java
                        )
                    )
                    .id(likes.id)
                    .build()
                dataListLikes.add(like)
            }


            dataListAdapterItems.clear()
            dataListPosts.forEach { posts ->
                getPostsLikesComments(posts)
            }
        }
        likesViewModel.liveDataList.observeForever(observer)
    }

    private fun initCommentObserver() {

        val viewModelComments =
            ViewModelProviders.of(requireActivity()).get(CommentsViewModel::class.java)
        val observer = Observer<List<CommentsTable>>() {
            dataListComments.clear()
            it.forEach { commentt ->
                val comment = Comments.Builder()
                    .createdTime(commentt.createdTime)
                    .content(commentt.content)
                    .post(Gson().fromJson(commentt.post, Posts::class.java))
                    .whoCommentedUser(
                        Gson().fromJson(
                            commentt.whoCommentedUser,
                            UserCloudGallery::class.java
                        )
                    )
                    .id(commentt.id).build()
                dataListComments.add(comment)

                dataListAdapterItems.clear()
                dataListPosts.forEach { posts ->
                    getPostsLikesComments(posts)
                }


            }

        }
        viewModelComments.allItemsLiveData.observeForever(observer)
    }

    private fun initPostsObserver() {


        val viewModelPosts =
            ViewModelProviders.of(requireActivity()).get(PostsViewModel::class.java)
        val observer = Observer<List<PostsTable>>() {
            dataListPosts.clear()
            it.forEach { posts ->
                val post = Posts.Builder()
                    .createdTime(posts.createdTime)
                    .image(posts.postImage)
                    .description(posts.description)
                    .whoPostedUser(
                        Gson().fromJson(
                            posts.whoPostedUser,
                            UserCloudGallery::class.java
                        )
                    )
                    .id(posts.postId)
                    .build()
                    dataListPosts.add(post)

            }
            initCommentObserver()
            initLikesObserver()
        }
        viewModelPosts.liveDataList.observeForever(observer)
    }


    private fun getListOfPosts() {
        dataListPosts.clear()
        adapterHomePosts.notifyDataSetChanged()

        ///reading one time


        val request =
            ModelQuery.list(Posts::class.java)

        Amplify.API.query(request, { response ->
            if (response.data != null) {

                response.data.forEach { posts ->

                //  dataListPosts.add(it.data as Posts)

                }
                ThreadUtils.runOnUiThread {
                    hideLoading()
                    adapterHomePosts.notifyDataSetChanged()
                }

            } else {
                printLog("no posts")
                ThreadUtils.runOnUiThread { hideLoading() }

            }
        }, {
            printLog("error getting posts")

            ThreadUtils.runOnUiThread { hideLoading() }
        })


        val subscription = Amplify.API.subscribe(
            ModelSubscription.onCreate(Posts::class.java),
            { Log.i(TAG, "Subscription established") },
            {

                ThreadUtils.runOnUiThread {


                        dataListPosts.add(it.data as Posts)
                        hideLoading()
                        adapterHomePosts.notifyDataSetChanged()

                }
            },
            {
                Log.e(TAG, "Subscription failed", it)
                ThreadUtils.runOnUiThread {
                    hideLoading()
                    adapterHomePosts.notifyDataSetChanged()
                }
            },
            {
                Log.i(TAG, "Subscription completed")
                ThreadUtils.runOnUiThread {
                    hideLoading()
                    adapterHomePosts.notifyDataSetChanged()
                }
            }
        )


//        Amplify.DataStore.query(
//            Posts::class.java,
//            { posts ->
//                while (posts.hasNext()) {
//                    val post = posts.next()
////                    Log.i(TAG, "User Name: ${user.name}")
//                    if (post.isPublic) {
//                        dataListPosts.add(post)
//                    }
//                }
//                ThreadUtils.runOnUiThread {
//                    adapterPosts.notifyDataSetChanged()
//                    hideLoading()
//
//                    if (dataListPosts.isEmpty()) {
//                        showNoDataLayout()
//                    } else {
//                        hideNoDataLayout()
//                    }
//
//                }
//            },
//            {
//                Log.e(TAG, "Query failed for posts", it)
//                ThreadUtils.runOnUiThread {
//                    adapterPosts.notifyDataSetChanged()
//                    hideLoading()
//                }
//            }
//        )
    }

    companion object {
        private val ARG_DATA = "position"
        fun newInstance(index: Int): PublicFeedFragment {
            val fragment = PublicFeedFragment()
            val args = Bundle()
            args.putInt(ARG_DATA, index)
            fragment.arguments = args
            return fragment
        }
    }

}