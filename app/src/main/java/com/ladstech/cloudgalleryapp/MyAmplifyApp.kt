package com.ladstech.cloudgalleryapp

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin

import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin

import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.DataStoreChannelEventName
import com.amplifyframework.datastore.events.NetworkStatusEvent
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import com.ladstech.cloudgalleryapp.utils.AppConstant.Companion.TAG


class MyAmplifyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        try {
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)
//
//            Amplify.Hub.subscribe(
//                HubChannel.DATASTORE,
//                { it.name == DataStoreChannelEventName.NETWORK_STATUS.toString()
//                },
//                {
//                    val networkStatus = it.data as NetworkStatusEvent
//                    Log.i(TAG, "User has a network connection? ${networkStatus.active}")
//
//                    if(networkStatus.active){
//                        Amplify.DataStore.start(
//                            { Log.i(TAG, "DataStore started") },
//                            { Log.e(TAG, "Error starting DataStore", it) }
//                        )
//                    }else{
//                        Amplify.DataStore.stop(
//                            { Log.i(TAG, "DataStore stopped ") },
//                            { Log.e(TAG, "Error stopping DataStore", it) }
//                        )
//                    }
//                }
//            )
            Log.d(
                TAG, "initialize Amplify"
            )
        } catch (error: AmplifyException) {
            Log.d(
                TAG, "Could not initialize Amplify", error
            )
        }


    }
}