package com.example.go4lunchjava.repository;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.places_api.PlacesApiService;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResponse;
import com.example.go4lunchjava.restaurant_details.RestaurantDetails;
import com.example.go4lunchjava.restaurant_details.RestaurantDetailsViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlacesApiRepository {

    //TODO: IP: 93.23.199.77
    private static final String BASE_URL = "https://maps.googleapis.com/";
    private static PlacesApiRepository sInstance;
    private Retrofit retrofit;
    private PlacesApiService service;

    //Caches
    private HashMap<String, NearBySearchResponse> mNearByCache = new HashMap<>();
    private HashMap<String, RestaurantDetailsResponse> mDetailsCache = new HashMap<>();

    //Data for ViewModels
    private MutableLiveData<RestaurantDetailsResponse> mDetailsResponseLiveData = new MutableLiveData<>();


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

    public LiveData<RestaurantDetailsResponse> getDetailsResponseLiveData(){
        return mDetailsResponseLiveData;
    }


    //--------------------------------------------------------------------------------------------//
    //                                      NEAR BY PLACES
    //--------------------------------------------------------------------------------------------//
    public NearBySearchResponse getNearBySearchResponse(LatLng latLng, int radius){

        double latitude = Math.floor(latLng.latitude * 10_000) / 10_000;
        double longitude = Math.floor(latLng.longitude * 10_000) / 10_000;

        String location = latitude + "," + longitude;
        NearBySearchResponse nearBySearchResponse = mNearByCache.get(location);

        if (nearBySearchResponse == null){
            try {
                Log.d("debuglog", "Places Api request...");
                nearBySearchResponse = service.nearbySearch(location, radius).execute().body();
                mNearByCache.put(location, nearBySearchResponse); //Used for map to prevent identical requests in the future

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.d("debuglog", "Places cache used");
        }

        return nearBySearchResponse;
    }

    //--------------------------------------------------------------------------------------------//
    //                                       PLACE DETAILS
    //--------------------------------------------------------------------------------------------//

    public void fetchDetailsResponseFromApi(String placeId){

        GetRestaurantDetailsAsyncTask asyncTask = new GetRestaurantDetailsAsyncTask(
                PlacesApiRepository.this, placeId);
        asyncTask.execute();
    }

    ////////////////////
    /////ASYNC TASK/////
    ////////////////////
    private static class GetRestaurantDetailsAsyncTask extends AsyncTask<Void, Void, RestaurantDetailsResponse> {

        //TODO NINO: Weak reference useful ?
        private WeakReference<PlacesApiRepository> mPlacesApiRepositoryReference; //In case instance in garbage collected
        private String mPlaceId;

        GetRestaurantDetailsAsyncTask(PlacesApiRepository placesApiRepository, String placeId){

            mPlacesApiRepositoryReference = new WeakReference<>(placesApiRepository);
            mPlaceId = placeId;

        }

        @Override
        protected RestaurantDetailsResponse doInBackground(Void... voids) {

            PlacesApiRepository placesRepo = mPlacesApiRepositoryReference.get();

            RestaurantDetailsResponse response = placesRepo.mDetailsCache.get(mPlaceId);

            //Cache logic
            if (response == null) {
                //Not in cache : make a request
                try {
                    Log.d("debuglog", "Details Api request...");
                    response = placesRepo.service.detailsSearch(mPlaceId).execute().body();
                    placesRepo.mDetailsCache.put(mPlaceId, response);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return response;
        }

        @Override
        protected void onPostExecute(RestaurantDetailsResponse response) {
            super.onPostExecute(response);

            if (mPlacesApiRepositoryReference.get() != null){
                mPlacesApiRepositoryReference.get().mDetailsResponseLiveData.setValue(response);
            }
        }
    }

    //HOURS
    //TODO Cache
    public RestaurantDetailsResponse getHoursDetails(String placeId){

        RestaurantDetailsResponse response = null;

        try {
            response = service.hoursDetailsSearch(placeId).execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
