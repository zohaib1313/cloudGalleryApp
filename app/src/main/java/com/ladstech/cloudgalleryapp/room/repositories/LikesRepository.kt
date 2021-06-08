package com.ladstech.cloudgalleryapp.room.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.ladstech.cloudgalleryapp.room.dao.LikesDao

import com.ladstech.cloudgalleryapp.room.entities.LikesTable
import com.ladstech.cloudgalleryapp.utils.AppConstant


class LikesRepository(private val LikesDao: LikesDao) {

    val liveData: LiveData<List<LikesTable>> = LikesDao.getAllItems()


    suspend fun insert(data: LikesTable) {
        LikesDao.insert(data)
    }

    suspend fun update(data: LikesTable) {
        LikesDao.update(data)
    }

    suspend fun delete(id: String) {
        LikesDao.delete(id)
    }

    suspend fun deleteTable() {
        LikesDao.deleteTable()
    }

    suspend fun count(id: String) {
        LikesDao.count(id)
    }
}