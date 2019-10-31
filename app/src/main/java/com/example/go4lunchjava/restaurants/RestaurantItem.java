package com.example.go4lunchjava.restaurants;

import android.graphics.drawable.Drawable;

public class RestaurantItem {

    private String mName;
    private int mDistance;
    private String mType;
    private String mAdresse;
    private int mOpeningHours;
    private int mScore;
    private int mWorkmates;
    private Drawable mPicture;

    public RestaurantItem(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
