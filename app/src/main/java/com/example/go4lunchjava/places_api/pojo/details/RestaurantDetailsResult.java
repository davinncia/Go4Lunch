package com.example.go4lunchjava.places_api.pojo.details;

import androidx.annotation.VisibleForTesting;

import com.example.go4lunchjava.places_api.pojo.Geometry;
import com.example.go4lunchjava.places_api.pojo.details.hours.OpeningHoursDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RestaurantDetailsResult {

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("rating")
    @Expose
    private float rating;

    @SerializedName("vicinity")
    @Expose
    private String vicinity;

    @SerializedName("photos")
    @Expose
    private RestaurantDetailsPhotos[] photos;

    @SerializedName("international_phone_number")
    @Expose
    private String international_phone_number;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @SerializedName("opening_hours")
    @Expose
    private OpeningHoursDetails openingHours;

    @VisibleForTesting
    public RestaurantDetailsResult(String website, String name, String placeId, float rating, String vicinity, String phoneNumber) {
        this.website = website;
        this.name = name;
        this.placeId = placeId;
        this.rating = rating;
        this.vicinity = vicinity;
        this.international_phone_number = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public String getName() {
        return name;
    }

    public String getPlaceId() {
        return placeId;
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

    public Geometry getGeometry() {
        return geometry;
    }

    public OpeningHoursDetails getOpeningHours() {
        return openingHours;
    }
}
