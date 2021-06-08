package com.ladstech.cloudgalleryapp.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*

import com.ladstech.cloudgalleryapp.room.entities.PostsTable


@Dao
interface PostsDao {


    @Query("SELECT * FROM ${PostsTable.TABLE_NAME}")
    fun getAllItems(): LiveData<List<PostsTable>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: PostsTable)


    @Query("DELETE FROM ${PostsTable.TABLE_NAME} WHERE ${PostsTable.POST_ID}=:id")
    suspend fun delete(id: String)

  //  @Query("UPDATE ${PostsTable.TABLE_NAME} SET ${NotificationsTable.STATUS} = :status WHERE ${NotificationsTable.NOTIFICATION_ID} =:id")

    @Update
    suspend fun update(data: PostsTable)

    @Query("DELETE FROM ${PostsTable.TABLE_NAME}")
    suspend fun deleteTable()


    @Query("SELECT COUNT() FROM ${PostsTable.TABLE_NAME} WHERE ${PostsTable.POST_ID} =:id")
    suspend fun count(id: String): Int

}