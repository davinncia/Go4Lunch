package com.example.go4lunchjava.map;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.repository.LocationRepository;
import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResponse;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResult;
import com.example.go4lunchjava.utils.NetworkConnectionLiveData;
import com.example.go4lunchjava.utils.SingleLiveEvent;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    //Repositories
    private LocationRepository mLocationRepository;
    private PlacesApiRepository mPlacesApiRepository;
    private UsersFireStoreRepository mFireStoreRepository;

    //Network
    private LiveData<Boolean> mActiveNetwork;

    //Location
    private MediatorLiveData<LatLng> mLocationMediatorLiveData = new MediatorLiveData<>();
    LiveData<LatLng> mLocationLiveData = mLocationMediatorLiveData;

    //SingleLiveEvent triggered when no location found
    private SingleLiveEvent mNoLocationEvent = new SingleLiveEvent();
    LiveData<SingleLiveEvent> mNoLocationLiveData = mNoLocationEvent;

    //List of Points of interest
    private MediatorLiveData<List<Poi>> mPoiListMediatorLiveData = new MediatorLiveData<>();
    public LiveData<List<Poi>> mPoiListLiveData = mPoiListMediatorLiveData;

    //Internal LiveData to react to map ready & location permission
    private MutableLiveData<Boolean> mapAvailable = new MutableLiveData<>();
    private MutableLiveData<Boolean> locationPermission = new MutableLiveData<>();

    private boolean cameraMoved = false;
    private int mRadius = 3000;


    public MapViewModel(Application application) {

        mLocationRepository = LocationRepository.getInstance(application);
        mPlacesApiRepository = PlacesApiRepository.getInstance();
        mFireStoreRepository = UsersFireStoreRepository.getInstance();

        mActiveNetwork = new NetworkConnectionLiveData(application.getApplicationContext());

        mLocationRepository.startLocationUpdates(true);

        //LOCATION
        mLocationMediatorLiveData.addSource(mLocationRepository.getLatLngLiveData(), this::updateDeviceLocation);
        // Return to last known location on resume
        mLocationMediatorLiveData.addSource(mapAvailable, available -> {
            if (available) updateDeviceLocation(mLocationRepository.getLatLngLiveData().getValue());
        });

        //POI
        //mPoiListMediatorLiveData.addSource(mapAvailable, available -> {
        //    if (available && mLocationLiveData.getValue() != null) { //Location not yet found on opening...
        //        MapViewModel.this.fetchNearByPlaces(mLocationLiveData.getValue());
        //    }
        //});

        mPoiListMediatorLiveData.addSource(mActiveNetwork, isConnected -> {
                if(isConnected && mLocationLiveData.getValue() != null)
                    mPlacesApiRepository.fetchNearByPlacesFromApi(mLocationLiveData.getValue(), mRadius);
        });

        //When first location has been found (not every update though).
        mPoiListMediatorLiveData.addSource(mLocationMediatorLiveData, latLng -> {
            if (latLng != null) {

                mPlacesApiRepository.fetchNearByPlacesFromApi(latLng, mRadius);

                //mPoiListMediatorLiveData.removeSource(mLocationLiveData); //Removing the source for performance, prioritizing long clicks demands
            }
        });

        //Map Poi given NearByResponse
        mPoiListMediatorLiveData.addSource(mPlacesApiRepository.getNearByResponseLiveData(), this::mapPoiList);

    }


    private void updateDeviceLocation(LatLng latLng) {
        Boolean map = mapAvailable.getValue();
        Boolean permission = locationPermission.getValue();
        if (map == null || permission == null) return;

        if (map && permission) {

            if (latLng != null) {

                if (!cameraMoved) {
                    mLocationMediatorLiveData.setValue(latLng); //Update location only if camera is not moving
                    Log.d("debuglog", "Update Device Location");
                }

            } else {
                mNoLocationEvent.call(); //Trigger SingleLiveEvent noLocation found
            }

        } else {
            mLocationRepository.startLocationUpdates(false); //Map not available -> Stopping updates
        }
    }

    //region get poi
    private void mapPoiList(NearBySearchResponse nearBySearchResponse) {

        if (nearBySearchResponse == null) return;
        List<Poi> poiList = new ArrayList<>();

        for (NearBySearchResult result : nearBySearchResponse.getResults()) {

            poiList.add(new Poi(result.getName(), result.getPlaceId(),
                    result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()));
        }

        mPoiListMediatorLiveData.setValue(poiList); //Sending data to view before making the request to FireStore
        checkWorkmateInterest(poiList);
    }
    //endregion

    private void setSearchedPoi(RestaurantDetailsResponse response) {

        RestaurantDetailsResult result = response.getResult();
        if (result == null) return;

        List<Poi> poiList = new ArrayList<>();

        poiList.add(new Poi(result.getName(), result.getPlaceId(), result.getGeometry().getLocation().getLat(),
                result.getGeometry().getLocation().getLng()));
        mPoiListMediatorLiveData.setValue(poiList); //Mark on map
        checkWorkmateInterest(poiList); //update color if workmate joining
    }

    private void checkWorkmateInterest(List<Poi> poiList) {

        mFireStoreRepository.getAllUserDocuments().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                String restaurantId = String.valueOf(document.get(Workmate.FIELD_RESTAURANT_ID));

                for (Poi poi : poiList) {
                    //Checking if workmate choice is on the map
                    if (poi.getId().equals(restaurantId)) {
                        poi.setPointerRes(R.drawable.ic_pointer_blue);
                    }
                }

            }
            mPoiListMediatorLiveData.setValue(poiList); //Updating view
        });
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

    void setCameraMoved(boolean moved) {
        this.cameraMoved = moved;
    }

    void setCustomLocation(LatLng latLng) {
        mLocationRepository.setCustomLatLng(latLng);
        mPlacesApiRepository.fetchNearByPlacesFromApi(latLng, mRadius);
    }

    void fetchSpecificPlace(String placeId, LatLng latLng) {
        if (placeId != null) {
            //GetNearByPlacesAsyncTask asyncTask = new GetNearByPlacesAsyncTask(MapViewModel.this, mPlacesApiRepository, latLng, 10);
            //asyncTask.execute();

            mPlacesApiRepository.fetchDetailsResponseFromApi(placeId);
            mPoiListMediatorLiveData.addSource(mPlacesApiRepository.getDetailsResponseLiveData(),
                    this::setSearchedPoi);
        }
    }
}