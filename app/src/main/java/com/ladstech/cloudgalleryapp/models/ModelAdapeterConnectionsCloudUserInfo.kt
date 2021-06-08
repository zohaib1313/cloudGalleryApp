package com.ladstech.cloudgalleryapp.models

import com.amplifyframework.datastore.generated.model.BlockedUsers
import com.amplifyframework.datastore.generated.model.Connections
import com.amplifyframework.datastore.generated.model.UserCloudGallery

class ModelAdapeterConnectionsCloudUserInfo {


    var connectionStatus = ""
    var isBlocked: Boolean = false
    lateinit var userCloudGallery: UserCloudGallery

}