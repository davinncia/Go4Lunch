package com.example.go4lunchjava.restaurant_list;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResponse;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResult;
import com.example.go4lunchjava.repository.FireStoreRepository;
import com.example.go4lunchjava.repository.LocationRepository;
import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.utils.RestaurantDataFormat;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RestaurantViewModel extends ViewModel {

    private FireStoreRepository mFireStoreRepository;
    private PlacesApiRepository mPlacesApiRepository;
    private LocationRepository mLocationRepository;

    //List of restaurants
    private MediatorLiveData<List<RestaurantItem>> mRestaurantsMediatorLiveData = new MediatorLiveData<>();
    LiveData<List<RestaurantItem>> mRestaurantsLiveData = mRestaurantsMediatorLiveData;

    //List of filtered restaurants
    private MutableLiveData<List<RestaurantItem>> mFilteredRestaurantsMutableLiveData = new MutableLiveData<>();
    LiveData<List<RestaurantItem>> mFilteredRestaurants = mFilteredRestaurantsMutableLiveData;

    //LatLng
    private MutableLiveData<LatLng> mLatLngLiveData = new MutableLiveData<>();


    public RestaurantViewModel(Application application){

        mFireStoreRepository = FireStoreRepository.getInstance();
        mPlacesApiRepository = PlacesApiRepository.getInstance();
        mLocationRepository = LocationRepository.getInstance(application);

        mLatLngLiveData.setValue(mLocationRepository.getLatLngLiveData().getValue());


    }

    void init(String specificPlaceId){

        if (specificPlaceId != null){
            //A place has been looked up on map
            GetRestaurantDetailsAsyncTask asyncTask = new GetRestaurantDetailsAsyncTask(
                    RestaurantViewModel.this, mPlacesApiRepository, specificPlaceId);
            asyncTask.execute();
        } else {
            //Getting the restaurants as soon as we've got a location
            mRestaurantsMediatorLiveData.addSource(mLatLngLiveData, latLng -> {
                GetNearByPlacesAsyncTask asyncTask = new GetNearByPlacesAsyncTask(
                        RestaurantViewModel.this, mPlacesApiRepository, latLng);
                asyncTask.execute();
                mRestaurantsMediatorLiveData.removeSource(mLatLngLiveData); //We don't want anymore updates
            });

        }
    }

    private void getRestaurantsNearBy(NearBySearchResponse response){

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
            LatLng restaurantLatLng = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
            String distanceString = RestaurantDataFormat.getDistanceFromRestaurant(mLatLngLiveData.getValue(), restaurantLatLng);
            //RATING
            int ratingResource = RestaurantDataFormat.getRatingResource(result.getRating());

            restaurants.add(new RestaurantItem(
                    name,
                    placeId,
                    address,
                    hours,
                    pictureUri,
                    distanceString,
                    ratingResource
            ));
        }
        mRestaurantsMediatorLiveData.setValue(restaurants);

        fetchNumberOfWorkmatesInRestaurant(restaurants);

        fetchOpeningHourDetails(restaurants);
    }

    private void getSpecificRestaurant(RestaurantDetailsResponse response){

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
        String hours = RestaurantDataFormat.getHoursFromOpeningHours(result.getOpeningHours());
        //PICTURE
        String pictureUri = "";
        if (result.getPhotos() != null && result.getPhotos().length > 0) {
            pictureUri = RestaurantDataFormat.getPictureUri(result.getPhotos()[0].getPhoto_reference());
        }
        //DISTANCE
        LatLng placeLatLng = new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng());
        String distanceString = RestaurantDataFormat.getDistanceFromRestaurant(mLatLngLiveData.getValue(), placeLatLng);
        //RATING
        double rate = result.getRating() != null ? result.getRating() : -1;
        int ratingResource = RestaurantDataFormat.getRatingResource((float) rate);


        restaurants.add(new RestaurantItem(name, placeId, address, hours, pictureUri, distanceString, ratingResource));

        fetchNumberOfWorkmatesInRestaurant(restaurants);
    }


    //WORKMATE JOINING
    private void fetchNumberOfWorkmatesInRestaurant(List<RestaurantItem> restaurants){
        Log.d("debuglog", "fetching workmates");

        mFireStoreRepository.getAllUserDocuments().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot document : queryDocumentSnapshots){

                for (RestaurantItem restaurant : restaurants) {

                    int nbr = 0;

                    if (Objects.requireNonNull(document.get(Workmate.FIELD_RESTAURANT_ID)).equals(restaurant.getPlaceId())
                            && !document.getId().equals(FirebaseAuth.getInstance().getUid())){
                        //We have a match !
                        Log.d("debuglog", "We have a match !");

                        RestaurantItem newItem = new RestaurantItem(restaurant.getName(), restaurant.getPlaceId(), restaurant.getAddress(),
                                restaurant.getHours(), restaurant.getPictureUrl(), restaurant.getDistance(), restaurant.getRatingResource());
                        newItem.setWorkmatesJoiningNbr(++nbr);

                        restaurants.set(restaurants.indexOf(restaurant), newItem);
                    }
                }
            }
/*
            //TODO NINO: ListAdapter bug. This order has to be precisely followed
            //1- Create a NEW item & NEW list (!= android id)
            RestaurantItem newItem = new RestaurantItem("test", "xxxxxx", "Rue du debug",
                    "", "", "", R.drawable.ic_star);
            List<RestaurantItem> newList = new ArrayList<>(restaurants);

            //3- Insert new item in the new list
            newList.set(1, newItem);
 */

            mRestaurantsMediatorLiveData.setValue(new ArrayList<>(restaurants)); //Otherwise listAdapter won't make any difference
        });

    }

    //HOURS
    private void fetchOpeningHourDetails(List<RestaurantItem> restaurants){

        GetRestaurantHoursAsyncTask hoursAsyncTask = new GetRestaurantHoursAsyncTask(
                RestaurantViewModel.this, mPlacesApiRepository, restaurants);
        hoursAsyncTask.execute();
    }


    //SEARCH FUNCTION
    void searchInRestaurantList(List<RestaurantItem> restaurants, CharSequence charSequence){

        List<RestaurantItem> filteredRestaurants = new ArrayList<>();
        String input = charSequence.toString().trim().toLowerCase();

        for (RestaurantItem restaurantItem : restaurants){
            if (restaurantItem.getName().toLowerCase().contains(input)){
                filteredRestaurants.add(restaurantItem);
            }
        }
        mFilteredRestaurantsMutableLiveData.setValue(filteredRestaurants);
    }


    ///////////////////
    ///////ASYNC///////
    ///////////////////

    //LIST RESTAURANTS
    private static class GetNearByPlacesAsyncTask extends AsyncTask<Void, Void, NearBySearchResponse> {

        private final WeakReference<RestaurantViewModel> mRestaurantViewModelReference; //WeakReference in case ViewModel instance is gone while async task -> garbage collector
        private PlacesApiRepository mPlacesApiRepository;
        private LatLng mLatLng;

        GetNearByPlacesAsyncTask(RestaurantViewModel restaurantViewModel, PlacesApiRepository placesApiRepository, LatLng latLng) {

            this.mRestaurantViewModelReference = new WeakReference<>(restaurantViewModel);
            this.mPlacesApiRepository = placesApiRepository;
            this.mLatLng = latLng;

        }

        @Override
        protected NearBySearchResponse doInBackground(Void... voids) {
            if (mLatLng == null) return null;
            return mPlacesApiRepository.getNearBySearchResponse(mLatLng);
        }

        @Override
        protected void onPostExecute(NearBySearchResponse nearBySearchResponse) {

            if (mRestaurantViewModelReference.get() != null) {
                mRestaurantViewModelReference.get().getRestaurantsNearBy(nearBySearchResponse);
            }
        }
    }

    //SPECIFIC SEARCH
    private static class GetRestaurantDetailsAsyncTask extends AsyncTask<Void, Void, RestaurantDetailsResponse>{

        private final WeakReference<RestaurantViewModel> mRestaurantViewModelWeakReference;
        private PlacesApiRepository mPlacesApiRepository;
        private String mPlaceId;

        GetRestaurantDetailsAsyncTask(RestaurantViewModel restaurantViewModel, PlacesApiRepository placesApiRepository, String placeId){

            this.mRestaurantViewModelWeakReference = new WeakReference<>(restaurantViewModel);
            this.mPlacesApiRepository = placesApiRepository;
            mPlaceId = placeId;

        }

        @Override
        protected RestaurantDetailsResponse doInBackground(Void... voids) {
            return mPlacesApiRepository.getRestaurantDetailsResponse(mPlaceId);
        }

        @Override
        protected void onPostExecute(RestaurantDetailsResponse response) {
            super.onPostExecute(response);

            if (mRestaurantViewModelWeakReference.get() != null && response != null){
                mRestaurantViewModelWeakReference.get().getSpecificRestaurant(response);
            }
        }
    }

    //HOURS DETAILS
    private static class GetRestaurantHoursAsyncTask extends AsyncTask<Void, Void, List<RestaurantItem>>{

        private final WeakReference<RestaurantViewModel> mRestaurantViewModelWeakReference;
        private PlacesApiRepository mPlacesApiRepository;
        private List<RestaurantItem> mRestaurants;

        GetRestaurantHoursAsyncTask(RestaurantViewModel restaurantViewModel, PlacesApiRepository placesApiRepository, List<RestaurantItem> restaurants){

            this.mRestaurantViewModelWeakReference = new WeakReference<>(restaurantViewModel);
            this.mPlacesApiRepository = placesApiRepository;
            this.mRestaurants = restaurants;
        }

        @Override
        protected List<RestaurantItem> doInBackground(Void... voids) {

            RestaurantDetailsResponse response;

            for (RestaurantItem item : mRestaurants){
                response = mPlacesApiRepository.getHoursDetails(item.getPlaceId());
                String hours = RestaurantDataFormat.getHoursFromOpeningHours(response.getResult().getOpeningHours());
                if (hours != null) item.setHoursDesc(hours);
            }
            return mRestaurants;
        }

        @Override
        protected void onPostExecute(List<RestaurantItem> restaurants) {
            super.onPostExecute(restaurants);

            if (mRestaurantViewModelWeakReference.get() != null){
                mRestaurantViewModelWeakReference.get().mRestaurantsMediatorLiveData.setValue(restaurants);
            }
        }
    }
}