package com.example.go4lunchjava.places_api;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.map.Poi;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesApiRepository {

    //TODO: IP: 93.23.199.77
    private static final String BASE_URL = "https://maps.googleapis.com/";

    private Retrofit retrofit;
    private PlacesApiService service;

    public PlacesApiRepository(){

        retrofit = getRetrofitInstance();
        service = retrofit.create(PlacesApiService.class);

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

    public NearBySearchResponse getNearMainThread(LatLng latLng){
        String location = latLng.latitude + "," + latLng.longitude;

        try {
            return service.nearbySearch(location).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MutableLiveData<NearBySearchResponse> getNear(LatLng latLng){
        MutableLiveData<NearBySearchResponse> near = new MutableLiveData<>();
        String location = latLng.latitude + "," + latLng.longitude;

        service.nearbySearch(location).enqueue(new Callback<NearBySearchResponse>() {
            @Override
            public void onResponse(Call<NearBySearchResponse> call, Response<NearBySearchResponse> response) {

                if (response.isSuccessful()) near.setValue(response.body());
            }

            @Override
            public void onFailure(Call<NearBySearchResponse> call, Throwable t) {
                near.setValue(null);
            }
        });
        return near;
    }

    public NearBySearchResponse getNearBySearchResponse(LatLng latLng){

        final NearBySearchResponse[] response = new NearBySearchResponse[1];
        String location = latLng.latitude + "," + latLng.longitude;

        service.nearbySearch(location).enqueue(new Callback<NearBySearchResponse>() {
            @Override
            public void onResponse(Call<NearBySearchResponse> call, Response<NearBySearchResponse> nearBySearchResponse) {
                response[0] = nearBySearchResponse.body();
                Log.d("debuglog", response[0].toString());
            }

            @Override
            public void onFailure(Call<NearBySearchResponse> call, Throwable t) {
                Log.e(PlacesApiRepository.class.getSimpleName(),"Error getting NearBySearchResponse: " + t);
            }
        });

        return response[0];
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
