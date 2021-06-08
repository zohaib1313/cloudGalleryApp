package com.ladstech.cloudgalleryapp.utils

import com.google.gson.annotations.SerializedName

class MyNotification {

    @SerializedName("title")
    var title: String = ""

    @SerializedName("body")
    var message: String = ""

    @SerializedName("type")
    var type: String = ""

    @SerializedName("type_id")
    var typeId: Int = 0

    override fun toString(): String {
        return "Notification(title='$title', message='$message', type='$type', typeId=$typeId)"
    }
}