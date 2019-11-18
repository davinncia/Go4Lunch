package com.example.go4lunchjava.restaurant_list;

import androidx.annotation.Nullable;

import java.util.Objects;

public class RestaurantItem {


    private String mName;
    private String mPlaceId;
    private String mDistance;
    private String mAddress;
    private String mHoursDesc;
    private int mRatingResource;
    private int mWorkmatesJoiningNbr = 0;
    private String mPictureUri;


    public RestaurantItem(String name, String placeId, String address, String openingHours, String pictureUri, String distance, int ratingResource) {
        mName = name;
        mPlaceId = placeId;
        mAddress = address;
        mHoursDesc = openingHours;
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

    public String getHours() {
        return mHoursDesc;
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

    public int getWorkmatesJoiningNbr() {
        return mWorkmatesJoiningNbr;
    }

    public void setWorkmatesJoingingNbr(int workmatesJoiningNbr) {
        mWorkmatesJoiningNbr = workmatesJoiningNbr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantItem that = (RestaurantItem) o;
        return mRatingResource == that.mRatingResource &&
                mWorkmatesJoiningNbr == that.mWorkmatesJoiningNbr &&
                mName.equals(that.mName) &&
                mDistance.equals(that.mDistance) &&
                mAddress.equals(that.mAddress) &&
                mHoursDesc.equals(that.mHoursDesc) &&
                mPictureUri.equals(that.mPictureUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mName, mDistance, mAddress, mHoursDesc, mRatingResource, mWorkmatesJoiningNbr, mPictureUri);
    }
}
