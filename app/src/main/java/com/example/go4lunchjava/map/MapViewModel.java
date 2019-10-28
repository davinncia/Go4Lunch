package com.example.go4lunchjava.map;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.repository.LocationRepository;
import com.google.android.gms.maps.model.LatLng;

public class MapViewModel extends ViewModel {

    private LocationRepository mLocationRepository;

    private MediatorLiveData<LatLng> mLocationMediatorLiveData = new MediatorLiveData<>();
    public LiveData<LatLng> mLocationLiveData = mLocationMediatorLiveData;

    // Internal LiveData to react to map ready & location permission
    private MutableLiveData<Boolean> mapAvailable = new MutableLiveData<>();
    private MutableLiveData<Boolean> locationPermission = new MutableLiveData<>();

    private boolean cameraMoved = false;

    public MapViewModel(Application application) {
        mLocationRepository = new LocationRepository(application);

        mapAvailable.setValue(false);
        locationPermission.setValue(false);


        mLocationMediatorLiveData.addSource(mLocationRepository.latLngLiveData, this::updateDeviceLocation);
        // Return to last known location on resume
        mLocationMediatorLiveData.addSource(mapAvailable, available -> {
                if(available) updateDeviceLocation(mLocationRepository.latLngLiveData.getValue());
        });
    }


    private void updateDeviceLocation(LatLng latLng){
        if (mapAvailable.getValue() && locationPermission.getValue() && !cameraMoved){
            mLocationRepository.startLocationUpdates(true);
            mLocationMediatorLiveData.setValue(latLng);
        } else {
            mLocationRepository.startLocationUpdates(false);
        }
    }

    public void hasMapAvailability(boolean available){
        mapAvailable.setValue(available);
    }

    public void hasLocationPermission(boolean granted){
        locationPermission.setValue(granted);
    }

    public void setCameraMoved(boolean moved){
        this.cameraMoved = moved;
    }
}
