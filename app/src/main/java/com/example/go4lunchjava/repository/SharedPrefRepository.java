package com.example.go4lunchjava.repository;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefRepository {


    private static final String SETTINGS_PREF = "settings_pref";

    private static final String NOTIF_PREF = "notif_pref";
    private static final String RADIUS_PREF = "radius_pref";

    private static SharedPrefRepository sInstance;

    private SharedPreferences mSettingsSharedPref;


    private SharedPrefRepository(Context context){
        mSettingsSharedPref = context.getSharedPreferences(SETTINGS_PREF, Context.MODE_PRIVATE);
    }

    public static SharedPrefRepository getInstance(Context context){
        if (sInstance == null){
            synchronized (SharedPrefRepository.class){
                if (sInstance == null){
                    sInstance = new SharedPrefRepository(context);
                }
            }
        }
        return sInstance;
    }

    public Boolean getNotifPref(){
        return mSettingsSharedPref.getBoolean(NOTIF_PREF, true);
    }

    public int getRadiusMetersPref(){
        return mSettingsSharedPref.getInt(RADIUS_PREF, 3000);
    }

    public void setNotifPref(Boolean enabled){
        mSettingsSharedPref.edit().putBoolean(NOTIF_PREF, enabled).apply();
    }

    public void setRadiusInMetters(int radius){
        mSettingsSharedPref.edit().putInt(RADIUS_PREF, radius).apply();
    }
}
