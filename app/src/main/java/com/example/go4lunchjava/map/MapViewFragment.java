package com.example.go4lunchjava.map;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.go4lunchjava.MainActivity;
import com.example.go4lunchjava.di.ViewModelFactory;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewFragment extends SupportMapFragment {

    private static final int RC_LOCATION_REQUEST = 100;
    private static final float ZOOM = 15;

    private GoogleMap mMap;

    private MapViewModel mMapViewModel;
    private LatLng mLatLng;

    public static MapViewFragment newInstance(){
        return new MapViewFragment();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        //TODO: What if wouldn't have to get Application ?
        ViewModelFactory factory = new ViewModelFactory(getActivity().getApplication());
        mMapViewModel = ViewModelProviders.of(this, factory).get(MapViewModel.class);

        mMapViewModel.mLocationLiveData.observe(this, latLng -> {
            mLatLng = latLng;
            if (mLatLng != null) {
                mMap.addMarker(new MarkerOptions().position(mLatLng).title("Marker in Alaska"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, ZOOM));
            }
        });

        mMapViewModel.hasLocationPermission(checkLocationPermission());

        getMapAsync(googleMap -> {
            mMap = googleMap;

            mMapViewModel.hasMapAvailability(true);
        });
    }

    ////////////////
    ///PERMISSION///
    ////////////////
    private Boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            return true; //GRANTED
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    RC_LOCATION_REQUEST);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == RC_LOCATION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            mMapViewModel.hasLocationPermission(true);
        } else {
            mMapViewModel.hasLocationPermission(false);
            Toast.makeText(getContext(), "Location not available.", Toast.LENGTH_SHORT).show();
        }
    }
}
