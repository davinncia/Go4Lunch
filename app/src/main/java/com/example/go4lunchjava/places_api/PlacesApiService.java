package com.example.go4lunchjava.places_api;

import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.restaurant_details.pojo_api.RestaurantDetailsResponse;
import com.google.gson.JsonElement;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface PlacesApiService {

    //location=40.7463956,-73.9852992
    @GET("maps/api/place/nearbysearch/json?type=restaurant&radius=10000&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4")
    Call<NearBySearchResponse> nearbySearch(
            @Query("location") String location
    );

    //place_id=ChIJJ4y_XahZwokRgO8olYwA7Cg
    @GET("maps/api/place/details/json?fields=name,photo,rating,vicinity,international_phone_number,website&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4")
    Call<RestaurantDetailsResponse> detailsSearch(
            @Query("place_id") String placeId
    );
}
