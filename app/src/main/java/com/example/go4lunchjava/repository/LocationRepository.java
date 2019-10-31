package com.example.go4lunchjava.repository;

import android.app.Application;
import android.os.Looper;
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


    private static LocationRepository sInstance;
    private Application mApplication;

    private MutableLiveData<LatLng> latLngMutableLiveData = new MutableLiveData<>();
    private LiveData<LatLng> latLngLiveData = latLngMutableLiveData;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null) {
                latLngMutableLiveData.setValue(new LatLng(locationResult.getLastLocation().getLatitude(),
                        locationResult.getLastLocation().getLongitude()));
                Log.d("debuglog", "LocationCallback");
            }
        }
    };


    private LocationRepository(Application application){
        this.mApplication = application;
        fusedLocationProviderClient  = LocationServices.getFusedLocationProviderClient(mApplication);
    }

    //Singleton Pattern
    public static LocationRepository getInstance(Application application){
        if (sInstance == null){
            sInstance = new LocationRepository(application);
        }
        return sInstance;
    }

    public void startLocationUpdates(boolean enabled){
        if (enabled) {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest
                    .setSmallestDisplacement(10) //meters
                    .setInterval(10000) //Updates every 10 seconds
                    .setFastestInterval(10000)
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null); // ?
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    public LiveData<LatLng> getLatLngLiveData() {
        return latLngLiveData;
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
