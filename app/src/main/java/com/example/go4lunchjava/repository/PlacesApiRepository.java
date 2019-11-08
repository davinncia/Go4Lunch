package com.example.go4lunchjava.repository;

import android.util.Log;

import com.example.go4lunchjava.places_api.PlacesApiService;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.restaurant_details.pojo_api.RestaurantDetailsResponse;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesApiRepository {

    //TODO: IP: 93.23.199.77
    private static final String BASE_URL = "https://maps.googleapis.com/";
    private static PlacesApiRepository sInstance;
    private Retrofit retrofit;
    private PlacesApiService service;

    //Cache
    //TODO: specify max using a stack || queue ?
    private HashMap<String, NearBySearchResponse> mCache = new HashMap<>();
    private static final String CACHE_KEY_MAP_REQUEST = "cache_key"; //Key for specific demand on map via double click

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
                mCache.put(CACHE_KEY_MAP_REQUEST, toStore); //Used for restaurant list
                return toStore;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public NearBySearchResponse getMapPlacesResponse(){

        NearBySearchResponse mapPlacesResponse = mCache.get(CACHE_KEY_MAP_REQUEST);

        /*
        //Necessary ?
        if (mapPlacesResponse == null) //Cache have been destroyed
            return getNearBySearchResponse(latLng); //Get a response from current location
        else
            return mapPlacesResponse;
         */

        return mapPlacesResponse;
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



    /*
    public void testPlaceApi(){

        List<NearBySearchResult> results = new ArrayList<>();
        List<Poi> poiList = new ArrayList<>();

        service.testMapsApi("44.4076406,0.2197464").enqueue(new Callback<NearBySearchResponse>() {
            @Override
            public void onResponse(Call<NearBySearchResponse> call, Response<NearBySearchResponse> response) {

                for (NearBySearchResult result : response.body().results){
                    poiList.add(new Poi(result.name, result.geometry.location.lat, result.geometry.location.lng));
                }

                for (Poi poi : poiList){

                    Log.d("debuglog", "name " + poi.getName());
                    Log.d("debuglog", "lat " + poi.getLat());
                    Log.d("debuglog", "long " + poi.getLon());
                    Log.d("debuglog", "______________________");

                }
            }

            @Override
            public void onFailure(Call<NearBySearchResponse> call, Throwable t) {
                Log.d("debuglog", "onFailure: " + t);

            }
        });
    }

     */


}
