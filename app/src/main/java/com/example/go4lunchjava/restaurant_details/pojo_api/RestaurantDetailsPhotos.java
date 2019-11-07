package com.example.go4lunchjava.restaurant_details.pojo_api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantDetailsPhotos {

    @SerializedName("photo_reference")
    @Expose
    private String photo_reference;

    @SerializedName("width")
    @Expose
    private String width;

    private String[] html_attributions;

    @SerializedName("height")
    @Expose
    private String height;

    public String getPhoto_reference() {
        return photo_reference;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }
}
