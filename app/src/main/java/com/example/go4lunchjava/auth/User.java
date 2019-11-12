package com.example.go4lunchjava.auth;

import android.net.Uri;

public class User {

    //TODO: maybe Workmate instead, seems it will only be used for workmate list
    public static final String FIELD_NAME = "user_name";
    public static final String FIELD_AVATAR = "avatar_uri";
    public static final String FIELD_RESTAURANT_ID = "restaurant";


    private String mDisplayName;
    private String mEmail;
    private String mAvatarUri;
    private String mRestaurantId;

    public User(String displayName, String email, String avatarUri, String restaurantId) {
        mDisplayName = displayName;
        mEmail = email;
        mAvatarUri = avatarUri;
        mRestaurantId = restaurantId;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getEmail() {
        return mEmail;
    }

    public String getAvatarUri() {
        return mAvatarUri;
    }

    public String getRestaurantId() {
        return mRestaurantId;
    }
}
