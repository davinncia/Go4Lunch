package com.example.go4lunchjava.restaurant_list;

public class RestaurantItem {

    //Unique id ?
    private String mName;
    private String mPlaceId;
    private String mDistance;
    private String mType;
    private String mAddress;
    private String mIsOpen;
    private float mRating;
    private int mWorkmates;
    private String mPictureReference;


    public RestaurantItem(String name, String placeId, String address, String openingHours, String pictureReference, String distance, float rating) {
        mName = name;
        mPlaceId = placeId;
        mAddress = address;
        mIsOpen = openingHours;
        mPictureReference = pictureReference;
        mDistance = distance;
        mRating = rating;
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

    public String getPictureUrl() {
        if (mPictureReference == null || mPictureReference.isEmpty()) return "";

        String link = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4"
                + "&photoreference="
                + mPictureReference;

        return link;
    }

    public String getDistance() {
        return mDistance;
    }

    public int getRating(){
        //Converting to notation on 3 instead of 5
        return Math.round(mRating * 3 / 5);
    }

    public String getPlaceId() {
        return mPlaceId;
    }
}
