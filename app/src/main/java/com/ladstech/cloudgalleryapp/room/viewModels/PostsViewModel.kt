package com.ladstech.cloudgalleryapp.room.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import com.ladstech.cloudgalleryapp.room.database.AppDatabase
import com.ladstech.cloudgalleryapp.room.entities.PostsTable
import com.ladstech.cloudgalleryapp.room.repositories.PostsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PostsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostsRepository
    public val liveDataList: LiveData<List<PostsTable>>

    init {
        val postsDao = AppDatabase.getDatabaseInstance(application.applicationContext).PostsDao()
        repository = PostsRepository(postsDao)
        liveDataList = repository.allItemsLiveData
    }


    fun insert(data: PostsTable) = CoroutineScope(Dispatchers.Main).launch {
        repository.insert(data)
    }

    fun delete(data: PostsTable) = CoroutineScope(Dispatchers.Main).launch {
        repository.delete(data)
    }

    fun deleteTable() = CoroutineScope(Dispatchers.Main).launch {
        repository.deleteTable()
    }


    fun update(data: PostsTable) = CoroutineScope(Dispatchers.Main).launch {
        repository.update(data)
    }

    fun count(id: String) = CoroutineScope(Dispatchers.Main).launch {
        repository.count(id)
    }

}