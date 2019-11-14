package com.example.go4lunchjava.restaurant_list;

import androidx.annotation.Nullable;

public class RestaurantItem {

    //Unique id ?
    private String mName;
    private String mPlaceId;
    private String mDistance;
    private String mType;
    private String mAddress;
    private String mIsOpen;
    private int mRatingResource;
    private int mWorkmates;
    private String mPictureUri;


    public RestaurantItem(String name, String placeId, String address, String openingHours, String pictureUri, String distance, int ratingResource) {
        mName = name;
        mPlaceId = placeId;
        mAddress = address;
        mIsOpen = openingHours;
        mPictureUri = pictureUri;
        mDistance = distance;
        mRatingResource = ratingResource;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public String isOpen() {
        return mIsOpen;
    }

    public String getPictureUrl() { return mPictureUri; }

    public String getDistance() {
        return mDistance;
    }

    public int getRatingResource(){
        return mRatingResource;
    }

    public String getPlaceId() {
        return mPlaceId;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }
}
