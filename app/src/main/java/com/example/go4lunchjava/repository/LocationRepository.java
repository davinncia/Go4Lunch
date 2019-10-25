package com.example.go4lunchjava.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class LocationRepository {

    private Application mApplication;
    private MutableLiveData<LatLng> latLngMutableLiveData = new MutableLiveData<>();
    public LiveData<LatLng> latLngLiveData = latLngMutableLiveData;

    public LocationRepository(Application application){
        this.mApplication = application;

        getLocation();
    }

    private void getLocation(){

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mApplication);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            //Careful, it can return null
            if (location != null) latLngMutableLiveData.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
        });
    }


}
