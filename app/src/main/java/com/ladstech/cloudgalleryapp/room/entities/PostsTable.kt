package com.ladstech.cloudgalleryapp.room.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson


@Entity(tableName = PostsTable.TABLE_NAME)
class PostsTable(

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = POST_ID)
    var postId: String = 0.toString(),

    @ColumnInfo(name = CREATED_TIME)
    var createdTime: String = "",

    @ColumnInfo(name = IMAGE)
    var postImage: String = "",

    @ColumnInfo(name = ISPUBLIC)
    var isPublic: Boolean = false,

    @ColumnInfo(name = TITLE)
    var title: String = "",

    @ColumnInfo(name = DESCRIPTION)
    var description: String = "",

    @ColumnInfo(name = WHOPOSTED)
    var whoPostedUser: String = "",

    ) {
    companion object {
        const val TABLE_NAME = "postsTable"
        const val POST_ID = "id"


        const val CREATED_TIME = "createdTime"
        const val IMAGE = "postImage"
        const val ISPUBLIC = "isPublic"
        const val TITLE = "title"
        const val DESCRIPTION = "description"

        const val WHOPOSTED = "whoPostedUser"


    }


    interface JSONConvertable {
        fun toJSON(): String = Gson().toJson(this)
    }

    inline fun <reified T : JSONConvertable> String.toObject(): T =
        Gson().fromJson(this, T::class.java)


}