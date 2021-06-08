package com.ladstech.cloudgalleryapp.room.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import com.ladstech.cloudgalleryapp.room.database.AppDatabase
import com.ladstech.cloudgalleryapp.room.entities.LikesTable
import com.ladstech.cloudgalleryapp.room.repositories.LikesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LikesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: LikesRepository
    public val liveDataList: LiveData<List<LikesTable>>

    init {
        val likeDao = AppDatabase.getDatabaseInstance(application.applicationContext).LikesDao()
        repository = LikesRepository(likeDao)
        liveDataList = repository.liveData
    }


    fun insert(data: LikesTable) = CoroutineScope(Dispatchers.Main).launch {
        repository.insert(data)
    }

    fun delete(id: String) = CoroutineScope(Dispatchers.Main).launch {
        repository.delete(id)
    }

    fun deleteTable() = CoroutineScope(Dispatchers.Main).launch {
        repository.deleteTable()
    }


    fun update(data: LikesTable) = CoroutineScope(Dispatchers.Main).launch {
        repository.update(data)
    }

    fun count(id: String) = CoroutineScope(Dispatchers.Main).launch {
        repository.count(id)
    }

}