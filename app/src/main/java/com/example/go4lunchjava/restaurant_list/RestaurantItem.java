package com.example.go4lunchjava.restaurant_list;

import java.util.Objects;

public class RestaurantItem {


    private String mName;
    private String mPlaceId;
    private String mDistance;
    private String mAddress;
    private String mHours;
    private int mRatingResource;
    private int mWorkmatesNbr;
    private String mPictureUri;


    public RestaurantItem(
            String name, String placeId, String address, String openingHours, String pictureUri,
            String distance, int ratingResource, int workmatesNbr) {
        this.mName = name;
        this.mPlaceId = placeId;
        this.mAddress = address;
        this.mHours = openingHours;
        this.mPictureUri = pictureUri;
        this.mDistance = distance;
        this.mRatingResource = ratingResource;
        this.mWorkmatesNbr = workmatesNbr;
    }


    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getHours() {
        return mHours;
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

    public int getWorkmatesNbr() {
        return mWorkmatesNbr;
    }

    public void setWorkmatesNbr(int workmatesNbr) {
        mWorkmatesNbr = workmatesNbr;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestaurantItem that = (RestaurantItem) o;

        return mWorkmatesNbr == that.mWorkmatesNbr && mHours.equals(that.mHours);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mName, mDistance, mAddress, mHours, mRatingResource, mWorkmatesNbr, mPictureUri);
    }
}
