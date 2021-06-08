package com.ladstech.cloudgalleryapp.room.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import com.ladstech.cloudgalleryapp.room.database.AppDatabase
import com.ladstech.cloudgalleryapp.room.entities.NotificationsTable
import com.ladstech.cloudgalleryapp.room.repositories.NotificationsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotificationsRepository
    val allCartItemsInViewModel: LiveData<List<NotificationsTable>>

    init {
        val cartDao = AppDatabase.getDatabaseInstance(application.applicationContext).notificationDao()
        repository = NotificationsRepository(cartDao)
        allCartItemsInViewModel = repository.allNotificationsLiveData
    }


    fun insert(notificationsTable: NotificationsTable) = CoroutineScope(Dispatchers.Main).launch {
        repository.insert(notificationsTable)
    }

    fun delete(notificationsTable: NotificationsTable) = CoroutineScope(Dispatchers.Main).launch {
        repository.delete(notificationsTable)
    }

    fun deleteTable() = CoroutineScope(Dispatchers.Main).launch {
        repository.deleteTable()
    }


    fun update(id: String, status: String) = CoroutineScope(Dispatchers.Main).launch {
        repository.update(id, status)
    }

    fun count(id: String) = CoroutineScope(Dispatchers.Main).launch {
        repository.count(id)
    }

}