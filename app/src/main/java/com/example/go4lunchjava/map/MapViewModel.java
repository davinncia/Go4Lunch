package com.example.go4lunchjava.map;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.example.go4lunchjava.repository.LocationRepository;
import com.example.go4lunchjava.utils.SingleLiveEvent;
import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    //Repositories
    private LocationRepository mLocationRepository;
    private PlacesApiRepository mPlacesApiRepository;

    //Location
    private MediatorLiveData<LatLng> mLocationMediatorLiveData = new MediatorLiveData<>();
    LiveData<LatLng> mLocationLiveData = mLocationMediatorLiveData;

    //SingleLiveEvent triggered when no location found
    private SingleLiveEvent mNoLocationEvent = new SingleLiveEvent();
    LiveData<SingleLiveEvent> mNoLocationLiveData = mNoLocationEvent;

    //List of Points of interest
    private MediatorLiveData<List<Poi>> mPoiListMediatorLiveData = new MediatorLiveData<>();
    LiveData<List<Poi>> mPoiListLiveData = mPoiListMediatorLiveData;

    // Internal LiveData to react to map ready & location permission
    private MutableLiveData<Boolean> mapAvailable = new MutableLiveData<>();
    private MutableLiveData<Boolean> locationPermission = new MutableLiveData<>();

    private boolean cameraMoved = false;


    public MapViewModel(Application application) {
        mLocationRepository = LocationRepository.getInstance(application);
        mPlacesApiRepository = PlacesApiRepository.getInstance();

        mLocationRepository.startLocationUpdates(true);

        mLocationMediatorLiveData.addSource(mLocationRepository.getLatLngLiveData(), this::updateDeviceLocation);
        // Return to last known location on resume
        mLocationMediatorLiveData.addSource(mapAvailable, available -> {
            if (available) updateDeviceLocation(mLocationRepository.getLatLngLiveData().getValue());
        });


        mPoiListMediatorLiveData.addSource(mapAvailable, available -> {
            Log.d("debuglog", "Map available triggering");
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
                    Log.d("debuglog", "updateDeviceLocation");
                }

            } else {
                mNoLocationEvent.call(); //Trigger SingleLiveEvent noLocation found
            }

        } else {
            mLocationRepository.startLocationUpdates(false); //Map not available -> Stopping updates
        }
    }

    private void getPoi(NearBySearchResponse nearBySearchResponse) {

        Log.d("debuglog", "Get Poi");
        if (nearBySearchResponse == null) return;
        Log.d("debuglog", "NearBySearchResponse");
        List<Poi> poiList = new ArrayList<>();
        for (NearBySearchResult result : nearBySearchResponse.results) {
            poiList.add(new Poi(result.name, result.geometry.location.lat, result.geometry.location.lng));
        }
        mPoiListMediatorLiveData.setValue(poiList);
    }

    public void hasMapAvailability(boolean available) {
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

    public void hasLocationPermission(boolean granted) {
        locationPermission.setValue(granted);
    }

    public void setCameraMoved(boolean moved) {
        this.cameraMoved = moved;
    }

    void fetchNearByPlaces(LatLng latLng){
        GetNearByPlacesAsyncTask asyncTask = new GetNearByPlacesAsyncTask(MapViewModel.this, mPlacesApiRepository, latLng);
        asyncTask.execute();
    }


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
}
