package com.example.go4lunchjava.map;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.places_api.PlacesApiRepository;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.example.go4lunchjava.repository.LocationRepository;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    private boolean FLAG = true;

    private LocationRepository mLocationRepository;
    private PlacesApiRepository mPlacesApiRepository;

    private MediatorLiveData<LatLng> mLocationMediatorLiveData = new MediatorLiveData<>();
    LiveData<LatLng> mLocationLiveData = mLocationMediatorLiveData;

    private MutableLiveData<NearBySearchResponse> mNearBySearchResponseMutableLiveData = new MutableLiveData<>();
    LiveData<NearBySearchResponse> mNearBySearchResponseLiveData = mNearBySearchResponseMutableLiveData;

    private MediatorLiveData<List<Poi>> mPoiListMediatorLiveData = new MediatorLiveData<>();
    LiveData<List<Poi>> mPoiListLiveData = mPoiListMediatorLiveData;

    // Internal LiveData to react to map ready & location permission
    private MutableLiveData<Boolean> mapAvailable = new MutableLiveData<>();
    private MutableLiveData<Boolean> locationPermission = new MutableLiveData<>();

    private boolean cameraMoved = false;

    public MapViewModel(Application application) {
        mLocationRepository = new LocationRepository(application);
        mPlacesApiRepository = new PlacesApiRepository();

        mapAvailable.setValue(false);
        locationPermission.setValue(false);

        mLocationMediatorLiveData.addSource(mLocationRepository.latLngLiveData, this::updateDeviceLocation);
        // Return to last known location on resume
        mLocationMediatorLiveData.addSource(mapAvailable, available -> {
            if (available) updateDeviceLocation(mLocationRepository.latLngLiveData.getValue());
        });


        mPoiListMediatorLiveData.addSource(mapAvailable, available -> {
            Log.d("debuglog", "Source trigering");
            if (available) {
                GetNearAsyncTask asyncTask = new GetNearAsyncTask();
                asyncTask.execute(mLocationLiveData.getValue());
            }
        });
    }


    private void updateDeviceLocation(LatLng latLng) {
        if (mapAvailable.getValue() && locationPermission.getValue() && !cameraMoved) {
            mLocationRepository.startLocationUpdates(true);
            mLocationMediatorLiveData.setValue(latLng);

            //DEBUG
            if (latLng != null && FLAG){
                GetNearAsyncTask async = new GetNearAsyncTask();
                async.execute(latLng);
                FLAG = false;
            }

        } else {
            mLocationRepository.startLocationUpdates(false);
        }
    }

    private void getPoi(NearBySearchResponse nearBySearchResponse) {

        Log.d("debuglog", "Get Poi");
       if (nearBySearchResponse == null) return;
       Log.d("debuglog", "We've got some response");
       List<Poi> poiList = new ArrayList<>();
       for (NearBySearchResult result : nearBySearchResponse.results) {
           poiList.add(new Poi(result.name, result.geometry.location.lat, result.geometry.location.lng));
       }
       mPoiListMediatorLiveData.setValue(poiList);
    }

    public void hasMapAvailability(boolean available) {
        mapAvailable.setValue(available);
    }

    public void hasLocationPermission(boolean granted) {
        locationPermission.setValue(granted);
    }

    public void setCameraMoved(boolean moved) {
        this.cameraMoved = moved;
    }

    //DEBUG
    private class GetNearAsyncTask extends AsyncTask<LatLng, Void, NearBySearchResponse>{

        @Override
        protected NearBySearchResponse doInBackground(LatLng... latLngs) {

            Log.d("debuglog", "doInBackground: ");
            if (latLngs[0] == null) return null;
            return mPlacesApiRepository.getNearMainThread(latLngs[0]);
        }

        @Override
        protected void onPostExecute(NearBySearchResponse nearBySearchResponse) {

            Log.d("debuglog", "onPostExecute");
            getPoi(nearBySearchResponse);
        }
    }
}
