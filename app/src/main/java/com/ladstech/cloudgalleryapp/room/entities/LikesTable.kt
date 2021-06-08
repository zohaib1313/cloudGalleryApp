package com.ladstech.cloudgalleryapp.room.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson


@Entity(tableName = LikesTable.TABLE_NAME)
class LikesTable(

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = ID)
    var id: String = 0.toString(),

    @ColumnInfo(name = POST)
    var postId: String = "",

    @ColumnInfo(name = USER_LIKED)
    var whoCommentedUser: String = "",


    ) {
    companion object {
        const val TABLE_NAME = "likesTable"
        const val ID = "id"
        const val POST = "post"
        const val USER_LIKED = "likeBy"



    }


    interface JSONConvertable {
        fun toJSON(): String = Gson().toJson(this)
    }

    inline fun <reified T : JSONConvertable> String.toObject(): T =
        Gson().fromJson(this, T::class.java)


}