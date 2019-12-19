package com.example.go4lunchjava.map;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.example.go4lunchjava.repository.NetworkRepository;
import com.example.go4lunchjava.repository.SharedPrefRepository;
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

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {

    //Repositories
    private LocationRepository mLocationRepo;
    private PlacesApiRepository mPlacesApiRepo;
    private UsersFireStoreRepository mUsersRepo;
    private NetworkRepository mNetworkRepo;

    //Location
    private MediatorLiveData<LatLng> mLocationMediatorLiveData = new MediatorLiveData<>();
    public LiveData<LatLng> mLocationLiveData = mLocationMediatorLiveData;

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
    private int mRadius;


    public MapViewModel(LocationRepository locationRepo, PlacesApiRepository placesApiRepo,
                        UsersFireStoreRepository usersRepo, NetworkRepository networkRepo,
                        SharedPrefRepository sharedPrefRepo) {

        this.mLocationRepo = locationRepo;
        this.mPlacesApiRepo = placesApiRepo;
        this.mUsersRepo = usersRepo;
        this.mNetworkRepo = networkRepo;

        mRadius = sharedPrefRepo.getRadiusMetersPref();

        mLocationRepo.startLocationUpdates(true);

        //LOCATION
        mLocationMediatorLiveData.addSource(mLocationRepo.getLatLngLiveData(), this::updateDeviceLocation);
        // Return to last known location on resume
        mLocationMediatorLiveData.addSource(mapAvailable, available -> {
            if (available) updateDeviceLocation(mLocationRepo.getLatLngLiveData().getValue());
        });

        //POI
        //Given network
        mPoiListMediatorLiveData.addSource(mNetworkRepo.getNetworkStatusLiveData(), isConnected -> {
                if(isConnected && mLocationLiveData.getValue() != null && mPoiListLiveData.getValue() == null)
                    mPlacesApiRepo.fetchNearByPlacesFromApi(mLocationLiveData.getValue(), mRadius);
        });

        //Given location changes
        mPoiListMediatorLiveData.addSource(mLocationMediatorLiveData, latLng -> {
            if (latLng != null) {
                mPlacesApiRepo.fetchNearByPlacesFromApi(latLng, mRadius);
            }
        });

        //Map Poi given NearByResponse
        mPoiListMediatorLiveData.addSource(mPlacesApiRepo.getNearByResponseLiveData(), this::mapPoiList);

        //Workmates
        mPoiListMediatorLiveData.addSource(mUsersRepo.getAllUserLiveData(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> userList) {
                List<Poi> poiList = mPoiListLiveData.getValue();
                if (poiList == null) return;

                for (User user : userList){

                    for (Poi poi : poiList) {

                        if (poi.getId().equals(user.getRestaurant_id())) {
                            //Someone is going there !
                            poi.setPointerRes(R.drawable.ic_pointer_blue);
                        }
                    }
                }
                mPoiListMediatorLiveData.setValue(poiList); //Updating view
            }
        });

    }

    private void updateDeviceLocation(LatLng latLng) {
        Boolean map = mapAvailable.getValue();
        Boolean permission = locationPermission.getValue();
        if (map == null || permission == null) return;

        if (map && permission) {

            if (latLng != null) {

                if (!cameraMoved) {
                    mLocationMediatorLiveData.setValue(latLng); //Update location only if camera is not moving
                }

            } else {
                mNoLocationEvent.call(); //Trigger SingleLiveEvent noLocation found
            }

        } else {
            mLocationRepo.startLocationUpdates(false); //Map not available -> Stopping updates
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
        mUsersRepo.fetchAllUsersDocuments(); //update color if workmate joining
    }
    //endregion

    private void setSearchedPoi(RestaurantDetailsResponse response) {

        RestaurantDetailsResult result = response.getResult();
        if (result == null) return;

        List<Poi> poiList = new ArrayList<>();

        poiList.add(new Poi(result.getName(), result.getPlaceId(), result.getGeometry().getLocation().getLat(),
                result.getGeometry().getLocation().getLng()));
        mPoiListMediatorLiveData.setValue(poiList); //Mark on map
        mUsersRepo.fetchAllUsersDocuments(); //update color if workmate joining
    }


    public void hasMapAvailability(boolean available) {
        mapAvailable.setValue(available);
        //Start location updates
        Boolean permission = locationPermission.getValue();
        if (permission == null) return;
        if (available && permission) {
            mLocationRepo.startLocationUpdates(true);
        } else {
            mLocationRepo.startLocationUpdates(false);
        }
    }

    public void hasLocationPermission(boolean granted) {
        locationPermission.setValue(granted);
    }

    public void setCameraMoved(boolean moved) {
        this.cameraMoved = moved;
    }

    String getPlaceIdFromName(String name){

        if (mPoiListLiveData.getValue() == null) return null;

        for (Poi poi : mPoiListLiveData.getValue()){
            if (poi.getName().equals(name))
                return poi.getId();
        }
        return null;
    }

    void setCustomLocation(LatLng latLng) {
        mLocationRepo.setCustomLatLng(latLng);
        mPlacesApiRepo.fetchNearByPlacesFromApi(latLng, mRadius);
    }

    void fetchSpecificPlace(String placeId, LatLng latLng) {
        if (placeId != null) {
            //GetNearByPlacesAsyncTask asyncTask = new GetNearByPlacesAsyncTask(MapViewModel.this, mPlacesApiRepo, latLng, 10);
            //asyncTask.execute();

            mPlacesApiRepo.fetchDetailsResponseFromApi(placeId);
            mPoiListMediatorLiveData.addSource(mPlacesApiRepo.getDetailsResponseLiveData(),
                    this::setSearchedPoi);
        }
    }
}