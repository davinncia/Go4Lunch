package com.example.go4lunchjava.restaurant_details;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.repository.FireStoreRepository;
import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResponse;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResult;
import com.example.go4lunchjava.utils.RestaurantDataFormat;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantDetailsViewModel extends ViewModel {

    //https://maps.googleapis.com/maps/api/place/details/json?place_id=ChIJJ4y_XahZwokRgO8olYwA7Cg&fields=name,photo,rating,vicinity,international_phone_number,website&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4

    private PlacesApiRepository mPlacesApiRepository;
    private FireStoreRepository mFireStoreRepository;
    private FirebaseAuth mAuth;

    //DETAILS
    private MutableLiveData<RestaurantDetails> mDetailsMutableLiveData = new MutableLiveData<>();
    LiveData<RestaurantDetails> mDetailsLiveData = mDetailsMutableLiveData;

    //WORKMATES
    private MutableLiveData<List<Workmate>> mWorkmatesMutable = new MutableLiveData<>();
    public LiveData<List<Workmate>> mWorkmatesLiveData = mWorkmatesMutable;

    //USER'S CHOICE
    private MutableLiveData<Boolean> mIsUserSelectionMutable = new MutableLiveData<>();
    public LiveData<Boolean> mIsUserSelectionLiveData = mIsUserSelectionMutable;

    //USER'S FAVORITE
    private MutableLiveData<Boolean> mIsUserFavMutable = new MutableLiveData<>();
    public LiveData<Boolean> mIsUserFavLiveData = mIsUserFavMutable;

    public RestaurantDetailsViewModel(){

        mPlacesApiRepository = PlacesApiRepository.getInstance();
        mFireStoreRepository = FireStoreRepository.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }

    void launchDetailsRequest(String placeId){

        GetRestaurantDetailsAsyncTask asyncTask = new GetRestaurantDetailsAsyncTask(
                RestaurantDetailsViewModel.this, mPlacesApiRepository, placeId);
        asyncTask.execute();
    }

    private void getDetails(RestaurantDetailsResponse response){

        if (response == null) return; //TODO: Show an error message ?

        RestaurantDetailsResult result = response.getResult();

        //NAME
        String name = result.getName();
        //RATING
        int ratingImageResource = RestaurantDataFormat.getRatingResource(result.getRating());
        //ADDRESS
        String address = result.getVicinity();
        //PICTURE
        String pictureUri = "";
        if(result.getPhotos() != null && result.getPhotos().length > 0) {
            pictureUri = RestaurantDataFormat.getPictureUri(result.getPhotos()[0].getPhoto_reference());
        }
        //PHONE
        String phoneNumber = result.getInternational_phone_number();
        //WEB
        String webSite = result.getWebsite();

        RestaurantDetails restaurantDetails = new RestaurantDetails(
                name,
                ratingImageResource,
                address,
                pictureUri,
                phoneNumber,
                webSite
        );

        mDetailsMutableLiveData.setValue(restaurantDetails);
    }

    void fetchFireStoreData(String placeId){

        mFireStoreRepository.getAllUserDocuments().addOnSuccessListener(queryDocumentSnapshots -> {

            List<Workmate> workmates = new ArrayList<>();

            for (QueryDocumentSnapshot document: queryDocumentSnapshots){

                //Checking if restaurant selected by current user
                if (document.getId().equals(mAuth.getUid())) {
                    //Current user
                    if (Objects.equals(document.get(Workmate.FIELD_RESTAURANT_ID), placeId)) {
                        mIsUserSelectionMutable.setValue(true);
                    } else {
                        mIsUserSelectionMutable.setValue(false);
                    }

                    List<String> favorites = (List<String>) document.get(Workmate.FIELD_FAVORITE_RESTAURANTS);

                    if (favorites != null && favorites.contains(placeId)) mIsUserFavMutable.setValue(true);
                    else mIsUserFavMutable.setValue(false);

                } else if (Objects.equals(document.get(Workmate.FIELD_RESTAURANT_ID), placeId)) {
                    //Every other users
                    workmates.add(new Workmate(String.valueOf(document.get(Workmate.FIELD_NAME)),
                            null,
                            String.valueOf(document.get(Workmate.FIELD_AVATAR)), null, null));
                    }
                }

            mWorkmatesMutable.setValue(workmates);
        });

    }

    void updateUserSelection(boolean selected, String placeId, String placeName){
        mIsUserSelectionMutable.setValue(selected);

        if (!selected) {
            placeId = "";
            placeName = "";
        }

        //Update FireStore
        mFireStoreRepository.updateRestaurantSelection(mAuth.getUid(), placeId, placeName);

    }

    void updateUserFavorite(boolean favorite, String placeId){
        mIsUserFavMutable.setValue(favorite);

        if (!favorite){
            //Remove from FireStore
            mFireStoreRepository.deleteFavoriteRestaurant(mAuth.getUid(), placeId);
        } else {
            //Add to FireStore
            mFireStoreRepository.addFavoriteRestaurants(mAuth.getUid(), placeId);
        }
    }

    private static class GetRestaurantDetailsAsyncTask extends AsyncTask<Void, Void, RestaurantDetailsResponse>{

        private WeakReference<RestaurantDetailsViewModel> mDetailsViewModelReference; //In case we loose ViewModel instance
        private PlacesApiRepository mPlacesApiRepository;
        private String mPlaceId;

        GetRestaurantDetailsAsyncTask(RestaurantDetailsViewModel detailsViewModel,
                                              PlacesApiRepository placesApiRepository, String placeId){

            mDetailsViewModelReference = new WeakReference<>(detailsViewModel);
            mPlacesApiRepository = placesApiRepository;
            mPlaceId = placeId;

        }

        @Override
        protected RestaurantDetailsResponse doInBackground(Void... voids) {
            return mPlacesApiRepository.getRestaurantDetailsResponse(mPlaceId);
        }

        @Override
        protected void onPostExecute(RestaurantDetailsResponse response) {
            super.onPostExecute(response);

            if (mDetailsViewModelReference.get() != null){
                mDetailsViewModelReference.get().getDetails(response);
            }
        }
    }

}
