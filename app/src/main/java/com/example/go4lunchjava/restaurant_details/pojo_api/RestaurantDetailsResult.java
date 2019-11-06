package com.example.go4lunchjava.restaurant_details.pojo_api;

public class RestaurantDetailsResult {

    private String website;

    private String name;

    private String rating;

    private String vicinity;

    private RestaurantDetailsPhotos[] photos;

    private String international_phone_number;


    public String getWebsite() {
        return website;
    }

    public String getName() {
        return name;
    }

    public String getRating() {
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
