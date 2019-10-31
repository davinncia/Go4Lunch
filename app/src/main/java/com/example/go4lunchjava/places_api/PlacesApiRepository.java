package com.example.go4lunchjava.places_api;

import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesApiRepository {

    //TODO: IP: 93.23.199.77
    private static final String BASE_URL = "https://maps.googleapis.com/";
    private static PlacesApiRepository sInstance;
    private Retrofit retrofit;
    private PlacesApiService service;

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

    public NearBySearchResponse getNearBySearchResponse(LatLng latLng){
        String location = latLng.latitude + "," + latLng.longitude;

        try {
            return service.nearbySearch(location).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
