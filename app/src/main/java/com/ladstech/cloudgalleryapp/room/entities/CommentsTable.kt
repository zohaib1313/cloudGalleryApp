package com.ladstech.cloudgalleryapp.room.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amplifyframework.datastore.generated.model.Comments
import com.amplifyframework.datastore.generated.model.Posts
import com.amplifyframework.datastore.generated.model.UserCloudGallery
import com.google.gson.Gson


@Entity(tableName = CommentsTable.TABLE_NAME)
class CommentsTable(

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = ID)
    var id: String = 0.toString(),

    @ColumnInfo(name = CREATED_TIME)
    var createdTime: String = "",

    @ColumnInfo(name = CONTENT)
    var content: String = "",

    @ColumnInfo(name = POST)
    var post: String = "",

    @ColumnInfo(name = USER_COMMENTED)
    var whoCommentedUser: String = "",


    ) {
    companion object {
        const val TABLE_NAME = "commentsTable"
        const val ID = "id"


        const val CREATED_TIME = "createdTime"
        const val CONTENT = "content"
        const val POST = "post"
        const val USER_COMMENTED = "commentedBy"

        fun commentObjFromCommentTable(table: CommentsTable): Comments {
            val commetObj = Comments.Builder()
                .content(table.content)
                .post(Gson().fromJson(table.post, Posts::class.java))
                .whoCommentedUser(Gson().fromJson(table.whoCommentedUser, UserCloudGallery::class.java))
                .id(table.id)
                .build()
            return commetObj

        }
    }


    interface JSONConvertable {
        fun toJSON(): String = Gson().toJson(this)
    }

    inline fun <reified T : JSONConvertable> String.toObject(): T =
        Gson().fromJson(this, T::class.java)

    override fun toString(): String {
        return "CommentsTable(id='$id', createdTime='$createdTime', content='$content', post='$post', whoCommentedUser='$whoCommentedUser')"
    }


}