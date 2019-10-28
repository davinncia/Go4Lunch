package com.example.go4lunchjava.places_api;

import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
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
}
