package com.ladstech.cloudgalleryapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ladstech.cloudgalleryapp.room.entities.NotificationsTable


@Dao
interface NotificationDao {


    @Query("SELECT * FROM ${NotificationsTable.TABLE_NAME}")
    fun getAllCartItems(): LiveData<List<NotificationsTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(data: NotificationsTable)


    @Query("DELETE FROM ${NotificationsTable.TABLE_NAME} WHERE ${NotificationsTable.NOTIFICATION_ID}=:id")
    suspend fun deleteNotification(id: String)

    @Query("UPDATE ${NotificationsTable.TABLE_NAME} SET ${NotificationsTable.STATUS} = :status WHERE ${NotificationsTable.NOTIFICATION_ID} =:id")
    suspend fun update(id: String, status: String)

    @Query("DELETE FROM ${NotificationsTable.TABLE_NAME}")
    suspend fun deleteTable()


    @Query("SELECT COUNT() FROM ${NotificationsTable.TABLE_NAME} WHERE ${NotificationsTable.NOTIFICATION_ID} =:id")
    suspend fun count(id: String): Int

}