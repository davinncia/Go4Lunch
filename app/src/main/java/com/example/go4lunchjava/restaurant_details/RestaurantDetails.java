package com.example.go4lunchjava.restaurant_details;

public class RestaurantDetails {

    private String mName;
    private int mRatingResource;
    private String mAddress;
    private String mPictureUri;
    private String mPhoneNumber;
    private String mWebSiteUrl;


    public RestaurantDetails(String name, int ratingResource, String address, String pictureUri, String phoneNumber, String webSiteUrl) {
        mName = name;
        mRatingResource = ratingResource;
        mAddress = address;
        mPictureUri = pictureUri;
        mPhoneNumber = phoneNumber;
        mWebSiteUrl = webSiteUrl;
    }

    public String getName() {
        return mName;
    }

    public int getRatingResource() {
        return mRatingResource;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getPictureUri() {
        return mPictureUri;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getWebSiteUrl() {
        return mWebSiteUrl;
    }
}
