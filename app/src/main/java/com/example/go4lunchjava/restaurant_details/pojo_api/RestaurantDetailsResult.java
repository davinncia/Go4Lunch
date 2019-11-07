package com.example.go4lunchjava.restaurant_details.pojo_api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantDetailsResult {

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("rating")
    @Expose
    private float rating;

    @SerializedName("vincinity")
    @Expose
    private String vicinity;

    @SerializedName("photos")
    @Expose
    private RestaurantDetailsPhotos[] photos;

    @SerializedName("international_phone_number")
    @Expose
    private String international_phone_number;

    public String getWebsite() {
        return website;
    }

    public String getName() {
        return name;
    }

    public Float getRating() {
        return rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public RestaurantDetailsPhotos[] getPhotos() {
        return photos;
    }

    public String getInternational_phone_number() {
        return international_phone_number;
    }
}
