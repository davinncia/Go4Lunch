package com.example.go4lunchjava.places_api.pojo.details;

import androidx.annotation.VisibleForTesting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantDetailsResponse {

    @SerializedName("result")
    @Expose
    private RestaurantDetailsResult result;

    @SerializedName("html_attributions")
    @Expose
    private String[] html_attributions;

    @SerializedName("status")
    @Expose
    private String status;

    @VisibleForTesting
    public RestaurantDetailsResponse(RestaurantDetailsResult result) {
        this.result = result;
    }

    public RestaurantDetailsResult getResult() {
        return result;
    }
}
