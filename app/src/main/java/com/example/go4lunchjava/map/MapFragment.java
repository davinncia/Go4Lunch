package com.example.go4lunchjava.map;


import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.di.ViewModelFactory;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;


public class MapFragment extends Fragment {

    private static final int RC_LOCATION_REQUEST = 100;
    private static final float ZOOM = 15;

    private MapViewModel mMapViewModel;

    private LatLng mLatLng;
    private GoogleMap mMap;
    private MapView mMapView;
    private FloatingActionButton mFab;

    private boolean hasLocation = false;


    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(){
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = rootView.findViewById(R.id.map_view);
        mFab = rootView.findViewById(R.id.fab_map_fragment);

        mMapView.onCreate(savedInstanceState); // Needed to display the map immediately

        mFab.setOnClickListener(view -> fabClick(view));


        //TODO: What if wouldn't have to get Application ?
        ViewModelFactory factory = new ViewModelFactory(getActivity().getApplication());
        mMapViewModel = ViewModelProviders.of(this, factory).get(MapViewModel.class);

        mMapViewModel.hasLocationPermission(checkLocationPermission());

        mMapViewModel.mLocationLiveData.observe(this, latLng -> {
            mLatLng = latLng;
            if (mLatLng != null) {
                mMap.addMarker(new MarkerOptions().position(mLatLng).title("Marker in Alaska"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, ZOOM));
                mFab.setImageResource(R.drawable.ic_map);
                hasLocation = true;

            } else {
                mFab.setImageResource(R.drawable.ic_workmates);
                hasLocation = false;
            }
        });

        mMapViewModel.mPoiListLiveData.observe(this, poiList -> {

            Log.d("debuglog", "onCreateView: POIIII");
            for (Poi poi : poiList){
                LatLng poiLatLng = new LatLng(poi.getLat(), poi.getLon());
                mMap.addMarker(new MarkerOptions().position(poiLatLng).title(poi.getName()));
            }
        });
        

        mMapView.getMapAsync(googleMap -> {
            mMap = googleMap;

            mMap.setOnCameraMoveListener(() -> {
                mMapViewModel.setCameraMoved(true);
                mMap.setOnCameraMoveListener(null);
            });

            mMapViewModel.hasMapAvailability(true);
        });

        return rootView;
    }

    private void fabClick(View view){

        if (hasLocation){
            mMapViewModel.setCameraMoved(false);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, ZOOM));
        } else {
            Snackbar.make(view, getResources().getString(R.string.location_null_message), Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();

        mMapViewModel.hasMapAvailability(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
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
