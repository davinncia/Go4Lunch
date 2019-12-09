package com.example.go4lunchjava.places_api;

import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface PlacesApiService {

    //location=40.7463956,-73.9852992
    //radius=3000
    @GET("maps/api/place/nearbysearch/json?type=restaurant&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4")
    Call<NearBySearchResponse> nearbySearch(
            @Query("location") String location,
            @Query("radius") int radius
    );

    //place_id=ChIJJ4y_XahZwokRgO8olYwA7Cg
    @GET("maps/api/place/details/json?fields=name,photo,rating,vicinity,international_phone_number,website,geometry,place_id,opening_hours&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4")
    Call<RestaurantDetailsResponse> detailsSearch(
            @Query("place_id") String placeId
    );

    //place_id=ChIJJ4y_XahZwokRgO8olYwA7Cg
    @GET("maps/api/place/details/json?fields=place_id,opening_hours&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4")
    Call<RestaurantDetailsResponse> hoursDetailsSearch(
            @Query("place_id") String placeId
    );
}
