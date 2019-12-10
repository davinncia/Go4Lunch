package com.example.go4lunchjava.repository;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.places_api.PlacesApiService;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResponse;
import com.example.go4lunchjava.places_api.pojo.details.hours.OpeningHoursDetails;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private MutableLiveData<NearBySearchResponse> mNearByResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<List<OpeningHoursDetails>> mHoursDetailLiveData = new MutableLiveData<>();


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

    //GETTERS
    public LiveData<RestaurantDetailsResponse> getDetailsResponseLiveData(){
        return mDetailsResponseLiveData;
    }

    public LiveData<NearBySearchResponse> getNearByResponseLiveData() {
        return mNearByResponseLiveData;
    }

    public LiveData<List<OpeningHoursDetails>> getHoursDetailLiveData() {
        return mHoursDetailLiveData;
    }

    //--------------------------------------------------------------------------------------------//
    //                                      NEAR BY PLACES
    //--------------------------------------------------------------------------------------------//
    public void fetchNearByPlacesFromApi(LatLng latLng, int radius){

        GetNearByPlacesAsyncTask asyncTask = new GetNearByPlacesAsyncTask(
                PlacesApiRepository.this, latLng, radius);
        asyncTask.execute();
    }

    //ASYNC TASK
    private static class GetNearByPlacesAsyncTask extends AsyncTask<Void, Void, NearBySearchResponse> {

        private final WeakReference<PlacesApiRepository> mPlacesApiRepoReference; //WeakReference in case instance is garbage collected
        private LatLng mLatLng;
        private int mRadius;

        GetNearByPlacesAsyncTask(PlacesApiRepository placesApiRepo, LatLng latLng, int radius) {

            this.mPlacesApiRepoReference = new WeakReference<>(placesApiRepo);
            this.mLatLng = latLng;
            this.mRadius = radius;
        }

        @Override
        protected NearBySearchResponse doInBackground(Void... voids) {
            if (mLatLng == null) return null;
            PlacesApiRepository placesRepo = mPlacesApiRepoReference.get();

            double latitude = Math.floor(mLatLng.latitude * 10_000) / 10_000;
            double longitude = Math.floor(mLatLng.longitude * 10_000) / 10_000;

            String location = latitude + "," + longitude;
            NearBySearchResponse nearBySearchResponse = placesRepo.mNearByCache.get(location);

            if (nearBySearchResponse == null){
                try {
                    Log.d("debuglog", "Places Api request...");
                    nearBySearchResponse = placesRepo.service.nearbySearch(location, mRadius).execute().body();
                    placesRepo.mNearByCache.put(location, nearBySearchResponse); //Used for map to prevent identical requests in the future

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Log.d("debuglog", "Places cache used");
            }

            return nearBySearchResponse;
        }

        @Override
        protected void onPostExecute(NearBySearchResponse nearBySearchResponse) {

            if (mPlacesApiRepoReference.get() != null) {
                mPlacesApiRepoReference.get().mNearByResponseLiveData.setValue(nearBySearchResponse);
            }
        }
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

    //--------------------------------------------------------------------------------------------//
    //                                        H O U R S
    //--------------------------------------------------------------------------------------------//
    //HOURS DETAILS
    public void fetchHoursDetails(List<String> restaurantIds){
        GetRestaurantHoursAsyncTask asyncTask = new GetRestaurantHoursAsyncTask(
                PlacesApiRepository.this, restaurantIds);
        asyncTask.execute();
    }

    private static class GetRestaurantHoursAsyncTask extends AsyncTask<Void, Void, List<OpeningHoursDetails>> {

        private WeakReference<PlacesApiRepository> mPlacesApiRepoReference;
        private List<String> mRestaurants;

        GetRestaurantHoursAsyncTask(PlacesApiRepository placesApiRepository, List<String> restaurantIds) {

            this.mPlacesApiRepoReference = new WeakReference<>(placesApiRepository);
            this.mRestaurants = restaurantIds;
        }

        @Override
        protected List<OpeningHoursDetails> doInBackground(Void... voids) {

            List<OpeningHoursDetails> hours = new ArrayList<>();

            for (String id : mRestaurants){

                RestaurantDetailsResponse response = null;

                try {
                    //TODO Cache
                    response = mPlacesApiRepoReference.get().service.hoursDetailsSearch(id).execute().body();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                assert response != null;
                hours.add(response.getResult().getOpeningHours());
            }
            return hours;
        }

        @Override
        protected void onPostExecute(List<OpeningHoursDetails> hours) {
            super.onPostExecute(hours);

            if (mPlacesApiRepoReference.get() != null) {
                mPlacesApiRepoReference.get().mHoursDetailLiveData.setValue(hours);
            }
        }
    }


}
