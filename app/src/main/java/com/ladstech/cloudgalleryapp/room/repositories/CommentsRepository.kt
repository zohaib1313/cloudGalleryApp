package com.ladstech.cloudgalleryapp.room.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.ladstech.cloudgalleryapp.room.dao.CommentsDao

import com.ladstech.cloudgalleryapp.room.entities.CommentsTable
import com.ladstech.cloudgalleryapp.utils.AppConstant


class CommentsRepository(private val commentsDao: CommentsDao) {

    val liveData: LiveData<List<CommentsTable>> = commentsDao.getAllItems()


    suspend fun insert(commentsTable: CommentsTable) {
        commentsDao.insert(commentsTable)
    }

    suspend fun update(commentsTable: CommentsTable) {
        commentsDao.update(commentsTable)
    }

    suspend fun delete(CommentsTable: CommentsTable) {
        commentsDao.delete(CommentsTable.id)
    }

    suspend fun deleteTable() {
        commentsDao.deleteTable()
    }

    suspend fun count(id: String) {
        commentsDao.count(id)
    }
}