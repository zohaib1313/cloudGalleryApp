package com.ladstech.cloudgalleryapp.models

import com.amplifyframework.datastore.generated.model.Comments
import com.amplifyframework.datastore.generated.model.Likes
import com.amplifyframework.datastore.generated.model.Posts

class ModelAdapterAllPosts {
    var likes = ArrayList<Likes>()
    var comments = ArrayList<Comments>()
    var haveILiked = false
    var post: Posts? = null


}