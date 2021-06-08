package com.ladstech.cloudgalleryapp.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ladstech.cloudgalleryapp.room.dao.CommentsDao
import com.ladstech.cloudgalleryapp.room.dao.LikesDao


import com.ladstech.cloudgalleryapp.room.dao.NotificationDao
import com.ladstech.cloudgalleryapp.room.dao.PostsDao
import com.ladstech.cloudgalleryapp.room.entities.CommentsTable
import com.ladstech.cloudgalleryapp.room.entities.LikesTable
import com.ladstech.cloudgalleryapp.room.entities.NotificationsTable

import com.ladstech.cloudgalleryapp.room.entities.PostsTable

@Database(entities = [PostsTable::class, NotificationsTable::class,CommentsTable::class,LikesTable::class], version = DB_VERSION)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationDao(): NotificationDao
    abstract fun PostsDao(): PostsDao
    abstract fun CommentsDao(): CommentsDao
    abstract fun LikesDao():LikesDao

    companion object {
        @Volatile
        private var databaseInstance: AppDatabase? = null

        fun getDatabaseInstance(mContext: Context): AppDatabase =
            databaseInstance ?: synchronized(this) {
                databaseInstance ?: buildDatabaseInstance(mContext).also {
                    databaseInstance = it
                }
            }

        private fun buildDatabaseInstance(mContext: Context) =
            Room.databaseBuilder(mContext, AppDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()

    }
}

const val DB_VERSION = 3
const val DB_NAME = "appDb.db"

