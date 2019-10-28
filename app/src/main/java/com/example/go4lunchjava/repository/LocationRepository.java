package com.example.go4lunchjava.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

public class LocationRepository {

    private Application mApplication;
    private MutableLiveData<LatLng> latLngMutableLiveData = new MutableLiveData<>();
    public LiveData<LatLng> latLngLiveData = latLngMutableLiveData;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                latLngMutableLiveData.setValue(new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude()));
            }
        }
    };

    public LocationRepository(Application application){
        this.mApplication = application;

        fusedLocationProviderClient  = LocationServices.getFusedLocationProviderClient(mApplication);
    }

    public void startLocationUpdates(boolean enabled){
        if (enabled) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10).setFastestInterval(2).setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null); // ?
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }





    /*
    USING GET FUSED LOCATION PROVIDER

    private void getLocation(){

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mApplication);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            //Careful, it can return null
            if (location != null) latLngMutableLiveData.setValue(new LatLng(location.getLatitude(), location.getLongitude()));
        });
    }
     */


}
