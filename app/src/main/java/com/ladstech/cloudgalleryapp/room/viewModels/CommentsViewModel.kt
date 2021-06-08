package com.ladstech.cloudgalleryapp.room.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import com.ladstech.cloudgalleryapp.room.database.AppDatabase
import com.ladstech.cloudgalleryapp.room.entities.CommentsTable

import com.ladstech.cloudgalleryapp.room.repositories.CommentsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CommentsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CommentsRepository
    public val allItemsLiveData: LiveData<List<CommentsTable>>

    init {
        val postsDao = AppDatabase.getDatabaseInstance(application.applicationContext).CommentsDao()
        repository = CommentsRepository(postsDao)
        allItemsLiveData = repository.liveData
    }


    fun insert(data: CommentsTable) = CoroutineScope(Dispatchers.Main).launch {
        repository.insert(data)
    }

    fun delete(data: CommentsTable) = CoroutineScope(Dispatchers.Main).launch {
        repository.delete(data)
    }

    fun deleteTable() = CoroutineScope(Dispatchers.Main).launch {
        repository.deleteTable()
    }


    fun update(data: CommentsTable) = CoroutineScope(Dispatchers.Main).launch {
        repository.update(data)
    }

    fun count(id: String) = CoroutineScope(Dispatchers.Main).launch {
        repository.count(id)
    }

}