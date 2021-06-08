package com.ladstech.cloudgalleryapp.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.amplifyframework.core.Amplify;

public class NetworkChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {
//                Log.e(AppConstant.TAG, "Connected to internet ");
//                       Amplify.DataStore.start(
//                                () -> Log.i(AppConstant.TAG, "DataStore started"),
//                                error -> Log.e(AppConstant.TAG, "Error starting DataStore: ", error)
//                        );
            } else {
//                Log.e(AppConstant.TAG, "Disconnected to internet !!! ");
//                Amplify.DataStore.stop(() -> {
//                            Log.e(AppConstant.TAG, " DataStore Stopped ");
//                        },
//                        error -> Log.e(AppConstant.TAG, "Error stopping DataStore: ", error));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}