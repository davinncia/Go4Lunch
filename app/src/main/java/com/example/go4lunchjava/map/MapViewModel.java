package com.example.go4lunchjava.map;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.repository.LocationRepository;
import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResponse;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResult;
import com.example.go4lunchjava.utils.SingleLiveEvent;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    //Repositories
    private LocationRepository mLocationRepository;
    private PlacesApiRepository mPlacesApiRepository;
    private UsersFireStoreRepository mFireStoreRepository;

    //Location
    private MediatorLiveData<LatLng> mLocationMediatorLiveData = new MediatorLiveData<>();
    LiveData<LatLng> mLocationLiveData = mLocationMediatorLiveData;

    //SingleLiveEvent triggered when no location found
    private SingleLiveEvent mNoLocationEvent = new SingleLiveEvent();
    LiveData<SingleLiveEvent> mNoLocationLiveData = mNoLocationEvent;

    //List of Points of interest
    private MediatorLiveData<List<Poi>> mPoiListMediatorLiveData = new MediatorLiveData<>();
    LiveData<List<Poi>> mPoiListLiveData = mPoiListMediatorLiveData;

    //Internal LiveData to react to map ready & location permission
    private MutableLiveData<Boolean> mapAvailable = new MutableLiveData<>();
    private MutableLiveData<Boolean> locationPermission = new MutableLiveData<>();

    private boolean cameraMoved = false;


    public MapViewModel(Application application) {

        mLocationRepository = LocationRepository.getInstance(application);
        mPlacesApiRepository = PlacesApiRepository.getInstance();
        mFireStoreRepository = UsersFireStoreRepository.getInstance();

        mLocationRepository.startLocationUpdates(true);

        mLocationMediatorLiveData.addSource(mLocationRepository.getLatLngLiveData(), this::updateDeviceLocation);
        // Return to last known location on resume
        mLocationMediatorLiveData.addSource(mapAvailable, available -> {
            if (available) updateDeviceLocation(mLocationRepository.getLatLngLiveData().getValue());
        });

        mPoiListMediatorLiveData.addSource(mapAvailable, available -> {
            if (available && mLocationLiveData.getValue() != null) { //Location not yet found on opening...
                MapViewModel.this.fetchNearByPlaces(mLocationLiveData.getValue());
            }
        });

        //When first location has been found (not every update though).
        mPoiListMediatorLiveData.addSource(mLocationMediatorLiveData, latLng -> {
            if (latLng != null){
                fetchNearByPlaces(latLng);
                //Removing the source for performance, prioritizing long clicks demands
                mPoiListMediatorLiveData.removeSource(mLocationLiveData);
            }
        });

    }


    private void updateDeviceLocation(LatLng latLng) {
        Boolean map = mapAvailable.getValue();
        Boolean permission = locationPermission.getValue();
        if (map == null || permission == null) return;

        if (map && permission) {

            if (latLng != null) {

                if (!cameraMoved) {
                    mLocationMediatorLiveData.setValue(latLng); //Update location only if camera is not moving
                    Log.d("debuglog", "Update Device Location");
                }

            } else {
                mNoLocationEvent.call(); //Trigger SingleLiveEvent noLocation found
            }

        } else {
            mLocationRepository.startLocationUpdates(false); //Map not available -> Stopping updates
        }
    }

    //region get poi
    private void getPoi(NearBySearchResponse nearBySearchResponse) {

        if (nearBySearchResponse == null) return;
        List<Poi> poiList = new ArrayList<>();

        for (NearBySearchResult result : nearBySearchResponse.getResults()) {

            poiList.add(new Poi(result.getName(), result.getPlaceId(),
                    result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()));
        }
        mPoiListMediatorLiveData.setValue(poiList); //Sending data to view before making the request to FireStore
        checkWorkmateInterest(poiList);
    }
//endregion

    private void setSearchedPoi(RestaurantDetailsResponse response){

        RestaurantDetailsResult result = response.getResult();
        if (result == null) return;

        List<Poi> poiList = new ArrayList<>();

        poiList.add(new Poi(result.getName(), result.getPlaceId(), result.getGeometry().getLocation().getLat(),
                result.getGeometry().getLocation().getLng()));
        mPoiListMediatorLiveData.setValue(poiList); //Mark on map
        checkWorkmateInterest(poiList); //update color if workmate joining
    }

    private void checkWorkmateInterest(List<Poi> poiList){

        mFireStoreRepository.getAllUserDocuments().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                String restaurantId = String.valueOf(document.get(Workmate.FIELD_RESTAURANT_ID));

                for (Poi poi : poiList){
                    //Checking if workmate choice is on the map
                    if (poi.getId().equals(restaurantId)) {
                        poi.setPointerRes(R.drawable.ic_pointer_blue);
                    }
                }

            }
            mPoiListMediatorLiveData.setValue(poiList); //Updating view
        });
    }


    void hasMapAvailability(boolean available) {
        mapAvailable.setValue(available);
        //Start location updates
        Boolean permission = locationPermission.getValue();
        if (permission == null) return;
        if (available && permission) {
            mLocationRepository.startLocationUpdates(true);
        } else {
            mLocationRepository.startLocationUpdates(false);
        }
    }

    void hasLocationPermission(boolean granted) {
        locationPermission.setValue(granted);
    }

    void setCameraMoved(boolean moved) {
        this.cameraMoved = moved;
    }

    void setCustomLocation(LatLng latLng){
        mLocationRepository.setCustomLatLng(latLng);
        fetchNearByPlaces(latLng);
    }

    private void fetchNearByPlaces(LatLng latLng){
        GetNearByPlacesAsyncTask asyncTask = new GetNearByPlacesAsyncTask(MapViewModel.this, mPlacesApiRepository, latLng);
        asyncTask.execute();
    }

    void fetchSpecificPlace(String placeId){
        if (placeId != null){
            GetRestaurantDetailsAsyncTask asyncTask = new GetRestaurantDetailsAsyncTask(MapViewModel.this, mPlacesApiRepository, placeId);
            asyncTask.execute();
        }
    }

    ///////////////////
    //////NEAR BY//////
    ///////////////////
    private static class GetNearByPlacesAsyncTask extends AsyncTask<Void, Void, NearBySearchResponse> {

        private final WeakReference<MapViewModel> mMapViewModelReference; //WeakReference in case ViewModel instance is gone while async task -> garbage collector
        private PlacesApiRepository mPlacesApiRepository;
        private LatLng mLatLng;

        GetNearByPlacesAsyncTask(MapViewModel mapViewModel, PlacesApiRepository placesApiRepository, LatLng latLng) {

            this.mMapViewModelReference = new WeakReference<>(mapViewModel);
            this.mPlacesApiRepository = placesApiRepository;
            this.mLatLng = latLng;

        }

        @Override
        protected NearBySearchResponse doInBackground(Void... voids) {
            if (mLatLng == null) return null;
            return mPlacesApiRepository.getNearBySearchResponse(mLatLng);
        }

        @Override
        protected void onPostExecute(NearBySearchResponse nearBySearchResponse) {

            if (mMapViewModelReference.get() != null) {
                mMapViewModelReference.get().getPoi(nearBySearchResponse);
            }
        }
    }

    ///////////////////
    //////SPECIFIC/////
    ///////////////////
    private static class GetRestaurantDetailsAsyncTask extends AsyncTask<Void, Void, RestaurantDetailsResponse>{

        private final WeakReference<MapViewModel> mMapViewModelReference;
        private PlacesApiRepository mPlacesApiRepository;
        private String mPlaceId;

        GetRestaurantDetailsAsyncTask(MapViewModel mapViewModel, PlacesApiRepository placesApiRepository, String placeId){

            this.mMapViewModelReference = new WeakReference<>(mapViewModel);
            this.mPlacesApiRepository = placesApiRepository;
            mPlaceId = placeId;

        }

        @Override
        protected RestaurantDetailsResponse doInBackground(Void... voids) {
            return mPlacesApiRepository.getRestaurantDetailsResponse(mPlaceId);
        }

        @Override
        protected void onPostExecute(RestaurantDetailsResponse response) {
            super.onPostExecute(response);

            if (mMapViewModelReference.get() != null && response != null){
                mMapViewModelReference.get().setSearchedPoi(response);
            }
        }
    }
}