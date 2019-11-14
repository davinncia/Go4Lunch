package com.example.go4lunchjava.map;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.di.ViewModelFactory;
import com.example.go4lunchjava.utils.BitmapConvertor;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class MapFragment extends Fragment {

    private static final int AUTO_COMPLETE_REQUEST_CODE = 1;

    private static final int RC_LOCATION_REQUEST = 100;
    private static final float ZOOM = 15;

    private MapViewModel mMapViewModel;

    private LatLng mLatLng;
    private GoogleMap mMap;
    private MapView mMapView;
    private FloatingActionButton mFab;

    private List<Poi> mPoiList;
    private boolean mWaitingToZoom = false;

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

        //SEARCH: Initialize Places SDK for auto complete
        Places.initialize(Objects.requireNonNull(getContext()), getResources().getString(R.string.google_place_api_key));

        mMapView = rootView.findViewById(R.id.map_view);
        mFab = rootView.findViewById(R.id.fab_map_fragment);

        mMapView.onCreate(savedInstanceState); // Needed to display the map immediately

        mFab.setOnClickListener(view -> fabClick(view));

        ViewModelFactory factory = new ViewModelFactory(getActivity().getApplication());
        mMapViewModel = ViewModelProviders.of(getActivity(), factory).get(MapViewModel.class);

        mMapViewModel.hasLocationPermission(checkLocationPermission());

        mMapView.getMapAsync(googleMap -> {
            mMap = googleMap;

            mMap.setOnMapLoadedCallback(() -> {
                Log.d("debuglog", "Map loaded !");
                if (mPoiList != null && mPoiList.size() > 0) { //A Poi list is waiting to be displayed
                    addPoiMarkers(mPoiList);
                    mPoiList.clear();
                }
                if (mWaitingToZoom){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, ZOOM));
                    mWaitingToZoom = false;
                }
                mMap.setOnMapLoadedCallback(null);
            });

            mMap.setOnCameraMoveListener(mCameraMoveListener);

            mMap.setOnMapLongClickListener(latLng -> {
                //Make a request to new PoiList at given location
                Toast.makeText(getContext(), getResources().getString(R.string.restaurant_search), Toast.LENGTH_SHORT).show();
                mMapViewModel.fetchNearByPlaces(latLng);
            });

            mMapViewModel.hasMapAvailability(true); //Triggers location updates
        });

        //Observe data
        mMapViewModel.mLocationLiveData.observe(this, latLng -> {
            mLatLng = latLng;

            if (mMap == null) { //Still loading in background
                mWaitingToZoom = true;
            } else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, ZOOM));
            mFab.setImageResource(R.drawable.ic_location);
            hasLocation = true;
        });

        mMapViewModel.mNoLocationLiveData.observe(this, o -> {
            mFab.setImageResource(R.drawable.ic_location_disabled);
            hasLocation = false;
        });

        mMapViewModel.mPoiListLiveData.observe(this, poiList -> {
            Log.d("debuglog", "Poi list triggered.");
            if (mMap == null) { //Google map initialize in background, thus can be null.
                //TODO: For some reason size = 0 on third demand...
                mPoiList = poiList; //We then keep the list to display when map is (finally) loaded.
            }
            else addPoiMarkers(poiList);
        });

        return rootView;
    }

    private void addPoiMarkers(List<Poi> poiList){
        mMap.clear();

        //BitmapDescriptor bitmapDescriptor = BitmapConvertor.bitmapDescriptorFromVector(getContext(), R.drawable.ic_restaurant);
        //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_restaurant);

        Log.d("debuglog", "Placing markers");
        for (Poi poi : poiList){
            BitmapDescriptor bitmapDescriptor = BitmapConvertor.getBitmapDescriptor(getContext(), poi.getPointerRes());
            LatLng poiLatLng = new LatLng(poi.getLat(), poi.getLon());
            mMap.addMarker(new MarkerOptions().icon(bitmapDescriptor)
                    .position(poiLatLng).title(poi.getName()));
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(poiList.get(0).getLat(), poiList.get(0).getLon()), ZOOM));
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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, ZOOM), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    mMap.setOnCameraMoveListener(mCameraMoveListener);
                }

                @Override
                public void onCancel() {
                }
            });

        } else {
            Snackbar.make(view, getResources().getString(R.string.location_null_message), Snackbar.LENGTH_SHORT).show();
            mMap.setOnCameraMoveListener(mCameraMoveListener);
        }
        mMapViewModel.setCameraMoved(false); // Allow viewModel to update camera position again
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

    ////////////////
    /////SEARCH/////
    ////////////////
    public void searchPlaceOnMap(){
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG,
                Place.Field.ADDRESS, Place.Field.OPENING_HOURS, Place.Field.RATING, Place.Field.PHOTO_METADATAS);
        //Places auto complete intent
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(Objects.requireNonNull(getContext()));
        startActivityForResult(intent, AUTO_COMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTO_COMPLETE_REQUEST_CODE){
            if (resultCode == AutocompleteActivity.RESULT_OK){
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                mMapViewModel.setSearchedPoi(place);
            }
        }
    }
}
