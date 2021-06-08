package com.ladstech.cloudgalleryapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ladstech.cloudgalleryapp.room.entities.CommentsTable




@Dao
interface CommentsDao {


    @Query("SELECT * FROM ${CommentsTable.TABLE_NAME}")
    fun getAllItems(): LiveData<List<CommentsTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: CommentsTable)


    @Query("DELETE FROM ${CommentsTable.TABLE_NAME} WHERE ${CommentsTable.ID}=:id")
    suspend fun delete(id: String)

    //  @Query("UPDATE ${CommentsTable.TABLE_NAME} SET ${NotificationsTable.STATUS} = :status WHERE ${NotificationsTable.NOTIFICATION_ID} =:id")

    @Update
    suspend fun update(data: CommentsTable)

    @Query("DELETE FROM ${CommentsTable.TABLE_NAME}")
    suspend fun deleteTable()


    @Query("SELECT COUNT() FROM ${CommentsTable.TABLE_NAME} WHERE ${CommentsTable.ID} =:id")
    suspend fun count(id: String): Int

}