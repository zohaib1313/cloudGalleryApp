package com.ladstech.cloudgalleryapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*

import com.ladstech.cloudgalleryapp.room.entities.LikesTable


@Dao
interface LikesDao {


    @Query("SELECT * FROM ${LikesTable.TABLE_NAME}")
    fun getAllItems(): LiveData<List<LikesTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: LikesTable)


    @Query("DELETE FROM ${LikesTable.TABLE_NAME} WHERE ${LikesTable.ID}=:id")
    suspend fun delete(id: String)

    //  @Query("UPDATE ${LikesTable.TABLE_NAME} SET ${NotificationsTable.STATUS} = :status WHERE ${NotificationsTable.NOTIFICATION_ID} =:id")

    @Update
    suspend fun update(data: LikesTable)

    @Query("DELETE FROM ${LikesTable.TABLE_NAME}")
    suspend fun deleteTable()


    @Query("SELECT COUNT() FROM ${LikesTable.TABLE_NAME} WHERE ${LikesTable.ID} =:id")
    suspend fun count(id: String): Int

}