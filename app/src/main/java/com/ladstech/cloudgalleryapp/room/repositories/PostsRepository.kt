package com.ladstech.cloudgalleryapp.room.repositories


import androidx.lifecycle.LiveData
import com.ladstech.cloudgalleryapp.room.dao.PostsDao
import com.ladstech.cloudgalleryapp.room.entities.PostsTable



class PostsRepository(private val postsDao: PostsDao) {

    val allItemsLiveData: LiveData<List<PostsTable>> = postsDao.getAllItems()


    suspend fun insert(data: PostsTable) {
        postsDao.insert(data)
    }

    suspend fun update(data: PostsTable) {
        postsDao.update(data)
    }

    suspend fun delete(data: PostsTable) {
        postsDao.delete(data.postId)
    }

    suspend fun deleteTable() {
        postsDao.deleteTable()
    }

    suspend fun count(id: String) {
        postsDao.count(id)
    }
}