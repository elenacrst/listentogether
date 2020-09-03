package com.elena.listentogether.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
//fixme idea: factory design pattern for sharedprefutils

public class SharedPrefUtils {
    public static final String KEY_PROFILE_USERNAME = "PROFILE_USERNAME";
    public static final String KEY_PROFILE_EMAIL = "PROFILE_EMAIL";
    public static final String KEY_PROFILE_ID = "PROFILE_ID";
    public static final String KEY_CURRENT_USER_TURN = "CURRENT_USER_TURN";
    public static final String KEY_REMINDER = "REMINDER";
    public static final String KEY_NOTIFICATION_MESSAGING = "NOTIFICATION_MESSAGING";
    public static final String KEY_NOTIFICATION_MEMBERS = "NOTIFICATION_MEMBERS";
    public static final String KEY_PROFILE_CITY = "PROFILE_CITY";
    public static final String KEY_PROFILE_COUNTRY = "PROFILE_COUNTRY";
    public static final String KEY_PROFILE_PHONE = "PROFILE_PHONE";
    public static final String KEY_PROFILE_AVATAR = "PROFILE_AVATAR";
    public static final String KEY_PROFILE_TYPE = "PROFILE_TYPE";

    private SharedPreferences mSharedPref;

    public SharedPrefUtils(Context context){
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void saveString(String key, String value){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public String getString(String key, String defaultValue){
        return mSharedPref.getString(key, defaultValue);
    }

    public void saveLong(String key, long value){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public long getLong(String key){
        return mSharedPref.getLong(key, 0);
    }

    public void saveBoolean(String key, boolean value){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defaultValue){
        return mSharedPref.getBoolean(key, defaultValue);
    }

    public void clear(){
        SharedPreferences.Editor editor = mSharedPref.edit();
        editor.clear();
        editor.apply();
    }
}
