package com.ladstech.cloudgalleryapp

import android.app.Application
import android.util.Log
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.api.graphql.model.ModelSubscription
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.Comments
import com.amplifyframework.datastore.generated.model.Likes
import com.amplifyframework.datastore.generated.model.Posts
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import com.google.gson.Gson
import com.ladstech.cloudgalleryapp.room.database.AppDatabase
import com.ladstech.cloudgalleryapp.room.entities.CommentsTable
import com.ladstech.cloudgalleryapp.room.entities.LikesTable
import com.ladstech.cloudgalleryapp.room.entities.PostsTable
import com.ladstech.cloudgalleryapp.room.repositories.CommentsRepository
import com.ladstech.cloudgalleryapp.room.repositories.LikesRepository
import com.ladstech.cloudgalleryapp.room.repositories.PostsRepository
import com.ladstech.cloudgalleryapp.utils.AppConstant
import com.ladstech.cloudgalleryapp.utils.AppConstant.Companion.TAG
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MyAmplifyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)
            Log.d(
                TAG, "initialize Amplify"
            )

        } catch (error: AmplifyException) {
            Log.d(
                TAG, "Could not initialize Amplify", error
            )
        }

        try {

            EmojiManager.install(GoogleEmojiProvider())
            loadPosts()
            loadComments()
            loadLikes()
        }catch (e:Exception){
            Log.d(
                TAG, "Exception in app class${e.localizedMessage.toString()}"
            )
        }


    }

    private fun loadPosts() {


        val postsDao = AppDatabase.getDatabaseInstance(this.applicationContext).PostsDao()
        var repository = PostsRepository(postsDao)


        CoroutineScope(Dispatchers.Main).launch {
            repository.deleteTable()
        }
        val request =
            ModelQuery.list(Posts::class.java)
        Amplify.API.query(request, { response ->
            if (response.data != null) {
                response.data.forEach { posts ->

                    val postsTable = PostsTable()
                    postsTable.postId = posts.id
                    postsTable.postImage = posts.image
                    postsTable.description = posts.description
                    postsTable.createdTime = posts.createdTime
                    postsTable.whoPostedUser = Gson().toJson(posts.whoPostedUser)
                    CoroutineScope(Dispatchers.Main).launch {
                        repository.insert(postsTable)
                    }


                }

            } else {

            }
        }, {

        })
//!!!!!!!!!!!!!!!  subscriber create !!!!!!!!!!!!!!!!/
        val subscription = Amplify.API.subscribe(
            ModelSubscription.onCreate(Posts::class.java),
            { Log.i(AppConstant.TAG, "post create Subscription established") },
            {
                Log.i(
                    AppConstant.TAG,
                    "Post create subscription received: app class ${(it.data as Posts).toString()}"
                )
                ThreadUtils.runOnUiThread {

                    val posts = it.data
                    //  dataListPosts.postValue(posts)
                    val postsTable = PostsTable()

                    postsTable.description = posts.description

                    postsTable.createdTime = posts.createdTime
                    postsTable.whoPostedUser = Gson().toJson(posts.whoPostedUser)
                    CoroutineScope(Dispatchers.Main).launch {
                        repository.insert(postsTable)
                    }
                }
            },
            {
                Log.e(AppConstant.TAG, "Subscription failed", it)
                ThreadUtils.runOnUiThread {
                    // hideLoading()
                }
            },
            {
                Log.i(AppConstant.TAG, "Post create Subscription completed")
                ThreadUtils.runOnUiThread {
                    //      hideLoading()
                }
            }
        )

//!!!!!!!!!!!!!!!  subscriber delete !!!!!!!!!!!!!!!!/


        val subscriptionDelete = Amplify.API.subscribe(
            ModelSubscription.onDelete(Posts::class.java),
            { Log.i(AppConstant.TAG, "post delete Subscription established") },
            {
                ThreadUtils.runOnUiThread {
                    Log.i(
                        AppConstant.TAG,
                        "Post delete subscription received appclass: ${(it.data as Posts).toString()}"
                    )
                    val posts = it.data
                    //  dataListPosts.add(it.data as Posts)
                    val postsTable = PostsTable()
                    postsTable.postId = posts.id

                    postsTable.description = posts.description

                    postsTable.createdTime = posts.createdTime
                    postsTable.whoPostedUser = Gson().toJson(posts.whoPostedUser)
                    CoroutineScope(Dispatchers.Main).launch {
                        repository.delete(postsTable)
                    }
                    //    hideLoading()

                }
            },
            {
                Log.e(AppConstant.TAG, " Post delete Subscription failed", it)

            },
            {
                Log.i(AppConstant.TAG, "Subscription completed")

            }
        )

    }

    private fun loadComments() {
        val commentsDao = AppDatabase.getDatabaseInstance(this.applicationContext).CommentsDao()
        val repository = CommentsRepository(commentsDao)
        CoroutineScope(Dispatchers.Main).launch {
            repository.deleteTable()
        }
        val request =
            ModelQuery.list(Comments::class.java)
        Amplify.API.query(request, { response ->
            if (response.data != null) {
                response.data.forEach { comments ->
                    val commentTable = CommentsTable()

                    commentTable.id = comments.id
                    commentTable.content = comments.content
                    commentTable.createdTime = comments.createdTime
                    commentTable.post = Gson().toJson(comments.post)
                    commentTable.whoCommentedUser = Gson().toJson(comments.whoCommentedUser)
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.insert(commentTable)
                    }

                }
            } else {

            }
        }, {

        })

        val subscription = Amplify.API.subscribe(
            ModelSubscription.onCreate(Comments::class.java),
            { Log.i(
                AppConstant.TAG,
                "comments create subscription establised"
            ) },
            {
                Log.i(
                    AppConstant.TAG,
                    "comments create subscription received: ${(it.data as Comments).content}"
                )
                (it.data as Comments).let { comments ->

                    ThreadUtils.runOnUiThread {
                        val commentTable = CommentsTable()
                        commentTable.id = comments.id
                        commentTable.content = comments.content
                        commentTable.createdTime = comments.createdTime
                        commentTable.post = Gson().toJson(comments.post)
                        commentTable.whoCommentedUser = Gson().toJson(comments.whoCommentedUser)
                        CoroutineScope(Dispatchers.Main).launch {
                            repository.insert(commentTable)
                        }

                    }
                }
            },
            {
                Log.e(AppConstant.TAG, "Subscription failed", it)
//                ThreadUtils.runOnUiThread {
//                    hideLoading()
//                }
            },
            {
                Log.i(AppConstant.TAG, "Subscription completed")
//                ThreadUtils.runOnUiThread {
//                    hideLoading()
//                }
            }
        )


    }

    private fun loadLikes() {
        val likesDao = AppDatabase.getDatabaseInstance(this.applicationContext).LikesDao()
        val repository = LikesRepository(likesDao)
        CoroutineScope(Dispatchers.Main).launch {
            repository.deleteTable()
        }
        val request =
            ModelQuery.list(Likes::class.java)
        Amplify.API.query(request, { response ->
            if (response.data != null) {
                response.data.forEach { likes ->
                    val likesTable = LikesTable()
                    likesTable.id = likes.id
                    likesTable.postId = likes.postId
                    likesTable.whoCommentedUser = Gson().toJson(likes.whoLikedUser)
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.insert(likesTable)
                    }

                }
            } else {
                printLog("like response is null")
            }
        }, {
            printLog("like exception  ${it.cause}")
        })

        val subscription = Amplify.API.subscribe(
            ModelSubscription.onCreate(Likes::class.java),
            {  Log.i(
                AppConstant.TAG,
                "likes create subscription establised"
            )},
            {
                Log.i(
                    AppConstant.TAG,
                    "Post like create subscription received: ${(it.data as Likes)}"
                )
                (it.data as Likes).let { likes ->
                    ThreadUtils.runOnUiThread {
                        val likeTable = LikesTable()
                        likeTable.id = likes.id
                        likeTable.whoCommentedUser = Gson().toJson(likes.whoLikedUser)
                        likeTable.postId = likes.postId

                        CoroutineScope(Dispatchers.Main).launch {
                            repository.insert(likeTable)
                        }
                    }
                }

            },
            {
                Log.e(AppConstant.TAG, "Subscription failed", it)
//                ThreadUtils.runOnUiThread {
//                    hideLoading()
//                }
            },
            {
                Log.i(AppConstant.TAG, "Subscription completed")
//                ThreadUtils.runOnUiThread {
//                    hideLoading()
//                }
            }
        )

        val subscriptionDelete = Amplify.API.subscribe(
            ModelSubscription.onDelete(Likes::class.java),
            {  Log.i(
                AppConstant.TAG,
                "likes delete subscription establised"
            )},
            {
                Log.i(
                    AppConstant.TAG,
                    "Post like delete subscription received: ${(it.data as Likes)}"
                )
                (it.data as Likes).let { likes ->
                    ThreadUtils.runOnUiThread {
                        val likeTable = LikesTable()
                        likeTable.id = likes.id
                        likeTable.whoCommentedUser = Gson().toJson(likes.whoLikedUser)
                        likeTable.postId = likes.postId

                        CoroutineScope(Dispatchers.IO).launch {
                            repository.delete(likeTable.id)
                        }
                    }
                }

            },
            {
                Log.e(AppConstant.TAG, "Subscription failed", it)
//                ThreadUtils.runOnUiThread {
//                    hideLoading()
//                }
            },
            {
                Log.i(AppConstant.TAG, "Subscription completed")
//                ThreadUtils.runOnUiThread {
//                    hideLoading()
//                }
            }
        )
    }

    private fun printLog(s: String) {
        Log.d(AppConstant.TAG, s)
    }


}