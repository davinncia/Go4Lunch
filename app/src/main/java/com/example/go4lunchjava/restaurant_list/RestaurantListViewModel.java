package com.example.go4lunchjava.restaurant_list;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResponse;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResult;
import com.example.go4lunchjava.places_api.pojo.details.hours.OpeningHoursDetails;
import com.example.go4lunchjava.repository.LocationRepository;
import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.utils.RestaurantDataFormat;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RestaurantListViewModel extends ViewModel {

    public static final String NEARBY_SEARCH = "nearby_search";

    private UsersFireStoreRepository mUsersRepo;
    private PlacesApiRepository mPlacesApiRepo;
    private FirebaseAuth mAuth;

    //List of restaurants
    private MediatorLiveData<List<RestaurantItem>> mRestaurantsMediatorLiveData = new MediatorLiveData<>();
    public LiveData<List<RestaurantItem>> mRestaurantsLiveData = mRestaurantsMediatorLiveData;

    //List of filtered restaurants
    private MutableLiveData<List<RestaurantItem>> mFilteredRestaurantsMutableLiveData = new MutableLiveData<>();
    LiveData<List<RestaurantItem>> mFilteredRestaurants = mFilteredRestaurantsMutableLiveData;

    //LatLng
    private MutableLiveData<LatLng> mLatLngLiveData = new MutableLiveData<>();

    private String mPlaceId;
    private int mRadius = 3000;


    public RestaurantListViewModel(Application application, UsersFireStoreRepository usersRepo, PlacesApiRepository placesRepo,
                                   FirebaseAuth firebaseAuth, LocationRepository locationRepo) {

        mUsersRepo = usersRepo;
        mPlacesApiRepo = placesRepo;
        mAuth = firebaseAuth;

        mLatLngLiveData.setValue(locationRepo.getLatLngLiveData().getValue());
    }

    public void init(String placeId) {

        if (mPlaceId == null){
            mPlaceId = placeId;
            addDataSourcesOncePlaceIdIsSpecified();
        }

    }

    private void addDataSourcesOncePlaceIdIsSpecified(){

        if (!mPlaceId.equals(NEARBY_SEARCH)) {
            //A place has been looked up on map
            mPlacesApiRepo.fetchDetailsResponseFromApi(mPlaceId);

            mRestaurantsMediatorLiveData.addSource(mPlacesApiRepo.getDetailsResponseLiveData(),
                    this::mapSpecificRestaurant);

        } else {
            //Getting nearby restaurants as soon as we've got a location
            mRestaurantsMediatorLiveData.addSource(mLatLngLiveData, latLng -> {

                mPlacesApiRepo.fetchNearByPlacesFromApi(latLng, mRadius);

                mRestaurantsMediatorLiveData.removeSource(mLatLngLiveData); //We don't want anymore updates
            });

            mRestaurantsMediatorLiveData.addSource(mPlacesApiRepo.getNearByResponseLiveData(),
                    this::mapNearByRestaurants);

            mRestaurantsMediatorLiveData.addSource(mUsersRepo.getAllUserLiveData(),
                    this::updateWorkmatesInterested);

            mRestaurantsMediatorLiveData.addSource(mPlacesApiRepo.getHoursDetailLiveData(),
                    this::mapRestaurantWithHoursDetails);
        }
    }

    //--------------------------------------------------------------------------------------------//
    //                                    NEAR BY RESTAURANTS
    //--------------------------------------------------------------------------------------------//
    private void mapNearByRestaurants(NearBySearchResponse response) {

        if (response == null) return;

        List<RestaurantItem> restaurants = new ArrayList<>();
        for (NearBySearchResult result : response.getResults()) {
            //NAME
            String name = result.getName();
            //ID
            String placeId = result.getPlaceId();
            //ADDRESS
            String address = result.getVicinity();
            //OPENING HOURS
            String hours = "Opening hours not communicated";
            if (result.getOpeningHours() != null) {
                if (result.getOpeningHours().getOpenNow()) hours = "Open now";
                else hours = "Closed";
            }
            //PICTURE
            String pictureUri = "";
            if (result.getPhotos() != null && result.getPhotos().size() > 0) {
                pictureUri = RestaurantDataFormat.getPictureUri(result.getPhotos().get(0).photoReference);
            }
            //DISTANCE
            String distanceString = "";
            if (result.getGeometry() != null) {
                LatLng restaurantLatLng = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
                distanceString = RestaurantDataFormat.getDistanceFromRestaurant(mLatLngLiveData.getValue(), restaurantLatLng);
            }
            //RATING
            int ratingResource = RestaurantDataFormat.getRatingResource(result.getRating());

            restaurants.add(new RestaurantItem(
                    name,
                    placeId,
                    address,
                    hours,
                    pictureUri,
                    distanceString,
                    ratingResource,
                    0
            ));
        }
        mRestaurantsMediatorLiveData.setValue(restaurants);

        mUsersRepo.fetchAllUsersDocuments();
    }

    //--------------------------------------------------------------------------------------------//
    //                                      SPECIFIC REQUEST
    //--------------------------------------------------------------------------------------------//
    private void mapSpecificRestaurant(RestaurantDetailsResponse response) {

        if (response == null) return;
        List<RestaurantItem> restaurants = new ArrayList<>();
        RestaurantDetailsResult result = response.getResult();

        //NAME
        String name = result.getName();
        //ID
        String placeId = result.getPlaceId();
        //ADDRESS
        String address = result.getVicinity();
        //OPENING HOURS
        String hours = RestaurantDataFormat.getHoursFromOpeningHours(result.getOpeningHours(), Calendar.getInstance(Locale.getDefault()));
        //PICTURE
        String pictureUri = "";
        if (result.getPhotos() != null && result.getPhotos().length > 0) {
            pictureUri = RestaurantDataFormat.getPictureUri(result.getPhotos()[0].getPhoto_reference());
        }
        //DISTANCE
        String distanceString = "";
        if (result.getGeometry() != null) {
            LatLng placeLatLng = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
            distanceString = RestaurantDataFormat.getDistanceFromRestaurant(mLatLngLiveData.getValue(), placeLatLng);
        }
        //RATING
        double rate = result.getRating() != null ? result.getRating() : -1;
        int ratingResource = RestaurantDataFormat.getRatingResource((float) rate);

        restaurants.add(new RestaurantItem(name, placeId, address, hours, pictureUri, distanceString, ratingResource, 0));
        mRestaurantsMediatorLiveData.setValue(restaurants);

        mUsersRepo.fetchAllUsersDocuments();
    }

    //--------------------------------------------------------------------------------------------//
    //                                  WORKMATES JOINING
    //--------------------------------------------------------------------------------------------//
    private void updateWorkmatesInterested(List<User> users){

        if (mRestaurantsMediatorLiveData.getValue() == null) return;

        List<RestaurantItem> result = new ArrayList<>();

        for (RestaurantItem item : mRestaurantsMediatorLiveData.getValue()) {

            RestaurantItem newItem = new RestaurantItem(
                    item.getName(), item.getPlaceId(), item.getAddress(), item.getHours(), item.getPictureUrl(),
                    item.getDistance(), item.getRatingResource(), item.getWorkmatesNbr());

            int nbr = 0;

            for (User user : users) {

                if (user.getRestaurant_id().equals(item.getPlaceId())
                        && !user.getId().equals(mAuth.getUid())) {
                    //We have a match !
                    nbr++;
                }
            }
            newItem.setWorkmatesNbr(nbr);
            result.add(newItem);
        }

        mRestaurantsMediatorLiveData.setValue(result);

        getRestaurantsHourDetails(result);
    }

    //--------------------------------------------------------------------------------------------//
    //                                      HOUR DETAILS
    //--------------------------------------------------------------------------------------------//
    private void getRestaurantsHourDetails(List<RestaurantItem> restaurants){

        List<String> ids = new ArrayList<>();
        for (RestaurantItem restaurant : restaurants){
            ids.add(restaurant.getPlaceId());
        }

        mPlacesApiRepo.fetchHoursDetails(ids);

    }

    private void mapRestaurantWithHoursDetails(List<OpeningHoursDetails> hourList){
        List<RestaurantItem> result = new ArrayList<>(); //We have to populate a new array for ListAdapter to be triggered

        if (mRestaurantsLiveData.getValue() == null || hourList.size() != mRestaurantsLiveData.getValue().size()){
            //Something went wrong fetching hours detail
            return;
        }

        int i = 0;
        for (RestaurantItem item : mRestaurantsLiveData.getValue()) {

            String hours = RestaurantDataFormat.getHoursFromOpeningHours(hourList.get(i), Calendar.getInstance(Locale.getDefault()));
            result.add(new RestaurantItem(item.getName(), item.getPlaceId(), item.getAddress(), hours,
                    item.getPictureUrl(), item.getDistance(), item.getRatingResource(), item.getWorkmatesNbr()));
            i++;
        }

        mRestaurantsMediatorLiveData.setValue(result);
    }


    //--------------------------------------------------------------------------------------------//
    //                                      SEARCH FUNCTION
    //--------------------------------------------------------------------------------------------//
    void searchInRestaurantList(List<RestaurantItem> restaurants, CharSequence charSequence) {

        List<RestaurantItem> filteredRestaurants = new ArrayList<>();
        String input = charSequence.toString().trim().toLowerCase();

        for (RestaurantItem restaurantItem : restaurants) {
            if (restaurantItem.getName().toLowerCase().contains(input)) {
                filteredRestaurants.add(restaurantItem);
            }
        }
        mFilteredRestaurantsMutableLiveData.setValue(filteredRestaurants);
    }

}