package com.ladstech.cloudgalleryapp.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.amplifyframework.datastore.generated.model.UserCloudGallery;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;


public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    SPManager mSPManager;
    UserCloudGallery user = null;
    // Context
    Context mContext;
    // Shared pref mode
    int PRIVATE_MODE = 0;

    private static SessionManager instance;



    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }


    private SessionManager(Context context) {
        this.mContext = context;
        pref = mContext.getSharedPreferences(AppConstant.PREF_NAME, PRIVATE_MODE);
        mSPManager = SPManager.getInstance(context);
        editor = pref.edit();
        getUser();
    }

    public SPManager getSPManager() {
        return mSPManager;
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(AppConstant.KEY_IS_LOGGED_IN, false);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(AppConstant.KEY_IS_LOGGED_IN, isLoggedIn);
        // commit changes
        editor.commit();

    }

    //
    public void clearSession() {
        editor.clear();
        getSPManager().put(AppConstant.IS_VISITED_INTRO, true);
        editor.commit();

    }


    public void createUserLoginSession(UserCloudGallery user) {
        clearSession();
        // Storing login value as TRUE
        editor.putBoolean(AppConstant.KEY_IS_LOGGED_IN, true);
        editor.putBoolean(AppConstant.IS_VISITED_INTRO, true);
        // commit changes
        editor.commit();

        updateUserSession(user);
    }






    public void updateUserSession(UserCloudGallery user) {
        this.user = user;
        // Storing login value as TRUE
        Gson gson = new Gson();
        String json = gson.toJson(user); // myObject - instance of MyObject
        // Storing in pref
        editor.putString(AppConstant.USER_INFO, json);

        // commit changes
        editor.commit();

    }



    public UserCloudGallery getUser() {
        if (user == null) {
            String str = pref.getString(AppConstant.USER_INFO, null);
            if (str != null) {
                user = new Gson().fromJson(str, UserCloudGallery.class);
            }
        }
        return user;
    }


    public void updateToken(@NotNull String token) {
        editor.putString(AppConstant.PUSH_TOKEN, token);
        editor.commit();
    }

    public String getToken() {
        return pref.getString(AppConstant.PUSH_TOKEN, "null");
    }

}
