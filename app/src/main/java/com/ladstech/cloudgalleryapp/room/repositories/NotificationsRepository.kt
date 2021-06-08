package com.ladstech.cloudgalleryapp.room.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import com.ladstech.cloudgalleryapp.room.dao.NotificationDao
import com.ladstech.cloudgalleryapp.room.entities.NotificationsTable
import com.ladstech.cloudgalleryapp.utils.AppConstant


class NotificationsRepository(private val notificationDao: NotificationDao) {

    val allNotificationsLiveData: LiveData<List<NotificationsTable>> = notificationDao.getAllCartItems()


    suspend fun insert(notificationsTable: NotificationsTable) {
        notificationDao.insertNotification(notificationsTable)
        Log.d(AppConstant.TAG, "notififacation item inserted")

    }

    suspend fun update(id: String, status: String) {
        notificationDao.update(id, status)
    }

    suspend fun delete(notificationsTable: NotificationsTable) {
        notificationDao.deleteNotification(notificationsTable.notificationId)
    }

    suspend fun deleteTable() {
        notificationDao.deleteTable()
    }

    suspend fun count(id: String) {
        notificationDao.count(id)
    }
}