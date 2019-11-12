package com.example.go4lunchjava.repository;

import android.util.Log;

import com.example.go4lunchjava.places_api.PlacesApiService;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.restaurant_details.pojo_api.RestaurantDetailsResponse;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesApiRepository {

    //TODO: IP: 93.23.199.77
    private static final String BASE_URL = "https://maps.googleapis.com/";
    private static PlacesApiRepository sInstance;
    private Retrofit retrofit;
    private PlacesApiService service;

    //Cache
    private HashMap<String, NearBySearchResponse> mCache = new HashMap<>();

    private PlacesApiRepository(){
        retrofit = getRetrofitInstance();
        service = retrofit.create(PlacesApiService.class);
    }

    //Singleton Pattern for multi-threading
    public static PlacesApiRepository getInstance(){

        if (sInstance == null){
            synchronized (PlacesApiRepository.class){
                if (sInstance == null){
                    sInstance = new PlacesApiRepository();
                }
            }
        }
        return sInstance;
    }


    private Retrofit getRetrofitInstance(){

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    ////////////////////
    ///////NEAR BY//////
    ////////////////////
    public NearBySearchResponse getNearBySearchResponse(LatLng latLng){

        double latitude = Math.floor(latLng.latitude * 10_000) / 10_000;
        double longitude = Math.floor(latLng.longitude * 10_000) / 10_000;

        String location = latitude + "," + longitude;
        NearBySearchResponse nearBySearchResponse = mCache.get(location);

        if (nearBySearchResponse != null){
            Log.d("debuglog", "Places cache used");
            return nearBySearchResponse;
        } else {
            try {
                Log.d("debuglog", "Places Api request...");
                NearBySearchResponse toStore = service.nearbySearch(location).execute().body();
                mCache.put(location, toStore); //Used for map to prevent identical requests in the future
                return toStore;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    ////////////////////
    ///////DETAILS//////
    ////////////////////
    public RestaurantDetailsResponse getRestaurantDetailsResponse(String placeId){

        RestaurantDetailsResponse response = null;

        try {
            response = service.detailsSearch(placeId).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;

    }

}
