package com.example.go4lunchjava.workmates_list;


public class Workmate {

    public static final String FIELD_NAME = "user_name";
    public static final String FIELD_RESTAURANT_ID = "restaurant_id";
    public static final String FIELD_RESTAURANT_NAME = "restaurant_name";
    public static final String FIELD_FAVORITE_RESTAURANTS = "favorites";

    private String mDisplayName;
    private String mUid;
    private String mAvatarUri;
    private String mRestaurantId; //No need
    private String mRestaurantName;

    public Workmate(String displayName, String uId, String avatarUri, String restaurantId, String restaurantName) {
        mDisplayName = displayName;
        mUid = uId;
        mAvatarUri = avatarUri;
        mRestaurantId = restaurantId;
        mRestaurantName = restaurantName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }

    public String getUid() {
        return mUid;
    }

    public String getAvatarUri() {
        return mAvatarUri;
    }

    public String getRestaurantId() {
        return mRestaurantId;
    }

    public String getRestaurantName() {
        return mRestaurantName;
    }
}
