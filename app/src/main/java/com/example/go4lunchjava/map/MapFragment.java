package com.example.go4lunchjava.map;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.di.ViewModelFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;


public class MapFragment extends Fragment {

    private static final int RC_LOCATION_REQUEST = 100;
    private static final float ZOOM = 15;

    private MapViewModel mMapViewModel;

    private LatLng mLatLng;
    private GoogleMap mMap;
    private MapView mMapView;
    private FloatingActionButton mFab;

    private boolean hasLocation = false;

    private MapFragment() {
        // Required empty constructor
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


        ViewModelFactory factory = new ViewModelFactory(getActivity().getApplication());
        mMapViewModel = ViewModelProviders.of(this, factory).get(MapViewModel.class);

        mMapViewModel.hasLocationPermission(checkLocationPermission());

        mMapViewModel.mLocationLiveData.observe(this, latLng -> {
                mLatLng = latLng;
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, ZOOM));
                mFab.setImageResource(R.drawable.ic_location);
                hasLocation = true;
        });

        mMapViewModel.mNoLocationLiveData.observe(this, o -> {
                mFab.setImageResource(R.drawable.ic_location_disabled);
                hasLocation = false;
                });

        mMapViewModel.mPoiListLiveData.observe(this, poiList -> {
            Log.d("debuglog", "Map View Update");
            mMap.clear();
            for (Poi poi : poiList){
                LatLng poiLatLng = new LatLng(poi.getLat(), poi.getLon());
                mMap.addMarker(new MarkerOptions().position(poiLatLng).title(poi.getName()));
            }
        });

        mMapView.getMapAsync(googleMap -> {
            mMap = googleMap;

            mMap.setOnCameraMoveListener(mCameraMoveListener);

            mMap.setOnMapLongClickListener(latLng -> {
                //Make a request to new PoiList at given location
                Toast.makeText(getContext(), getResources().getString(R.string.restaurant_search), Toast.LENGTH_SHORT).show();
                mMapViewModel.fetchNearByPlaces(latLng);
            });

            mMapViewModel.hasMapAvailability(true); //Triggers location updates
        });

        return rootView;
    }

    private GoogleMap.OnCameraMoveListener mCameraMoveListener = new GoogleMap.OnCameraMoveListener() {
        @Override
        public void onCameraMove() {
            Log.d("debuglog", "Moved !");
            mMapViewModel.setCameraMoved(true); //Stopping location updates
            mMap.setOnCameraMoveListener(null); //Saving performance
        }
    };

    private void fabClick(View view){

        if (hasLocation){
            //Animate camera to current position then resetting our CameraMoveListener (performance saving).
            mMapViewModel.setCameraMoved(false);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, ZOOM), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mMap.setOnCameraMoveListener(mCameraMoveListener);
                }

                @Override
                public void onCancel() {
                }
            });

            mMapViewModel.setCameraMoved(false); // Allow viewModel to update camera position again

        } else {
            Snackbar.make(view, getResources().getString(R.string.location_null_message), Snackbar.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        //Careful, do not pretend map is available here
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        //mMapViewModel.hasMapAvailability(false); //Stops location updates
        //Trouble: getMapAsync is not called between fragments.
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
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            return true; //GRANTED
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
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
