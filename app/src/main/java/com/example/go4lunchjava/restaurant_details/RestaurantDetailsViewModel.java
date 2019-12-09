package com.example.go4lunchjava.restaurant_details;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.VisibleForTesting;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.go4lunchjava.NotificationWorker;
import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResponse;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResult;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.utils.RestaurantDataFormat;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class RestaurantDetailsViewModel extends ViewModel {

    //https://maps.googleapis.com/maps/api/place/details/json?place_id=ChIJJ4y_XahZwokRgO8olYwA7Cg&fields=name,photo,rating,vicinity,international_phone_number,website&key=AIzaSyDSpFo8O861EgPYmsRlICS0sRs0zGEsrS4
    private static final String NOTIF_TAG = "notification";

    private PlacesApiRepository mPlacesApiRepo;
    private UsersFireStoreRepository mFireStoreUserRepo;
    private Application mApplication;
    private FirebaseAuth mAuth;

    //DETAILS
    public LiveData<RestaurantDetails> mDetailsLiveData;

    //WORKMATES
    public LiveData<List<Workmate>> mWorkmatesLiveData;

    //USER'S CHOICE
    private MutableLiveData<Boolean> mIsUserSelectionMutable = new MutableLiveData<>();
    public LiveData<Boolean> mIsUserSelectionLiveData = mIsUserSelectionMutable;

    //USER'S FAVORITE
    private MutableLiveData<Boolean> mIsUserFavMutable = new MutableLiveData<>();
    public LiveData<Boolean> mIsUserFavLiveData = mIsUserFavMutable;

    private String mPlaceId;

    public RestaurantDetailsViewModel(Application application, PlacesApiRepository placesRepo,
                                      UsersFireStoreRepository usersRepo, FirebaseAuth auth){

        this.mPlacesApiRepo = placesRepo;
        this.mFireStoreUserRepo = usersRepo;
        this.mApplication = application;
        this.mAuth = auth;

        //Mapping UiModel given api response
        mDetailsLiveData = Transformations.map(mPlacesApiRepo.getDetailsResponseLiveData(), detailsResponse -> {

            if (detailsResponse == null) return null; //TODO: Show an error message ?

            RestaurantDetailsResult result = detailsResponse.getResult();

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

            return restaurantDetails;
        });

        //Mapping workmates given FireStore response
        mWorkmatesLiveData = Transformations.map(mFireStoreUserRepo.getAllUserLiveData(), users -> {

            List<Workmate> workmates = new ArrayList<>();

            for (User user: users){

                //Checking if restaurant selected by current user
                if (user.getId().equals(mAuth.getUid())) {
                    //Current user
                    if (Objects.equals(user.getRestaurant_id(), mPlaceId)) {
                        mIsUserSelectionMutable.setValue(true);
                    } else {
                        mIsUserSelectionMutable.setValue(false);
                    }

                    List<String> favorites = user.getFavorites();

                    if (favorites != null && favorites.contains(mPlaceId)) mIsUserFavMutable.setValue(true);
                    else mIsUserFavMutable.setValue(false);

                } else if (Objects.equals(user.getRestaurant_id(), mPlaceId)) {
                    //Every other users
                    workmates.add(new Workmate(String.valueOf(user.getUser_name()), null,
                            String.valueOf(user.getAvatar_uri()), null, null));
                }
            }

            return workmates;
        });
    }

    public void launchDetailsRequest(String placeId){

        mPlaceId = placeId;
        mPlacesApiRepo.fetchDetailsResponseFromApi(placeId);

    }

    public void fetchFireStoreData(){

        mFireStoreUserRepo.fetchAllUsersDocuments();
    }


    void updateUserSelection(boolean selected, String placeId, String placeName){
        mIsUserSelectionMutable.setValue(selected);

        if (!selected) {
            placeId = "";
            placeName = "";
            disableNotification();
        } else {
            setNotification(placeName, placeId);
        }

        //Update FireStore
        mFireStoreUserRepo.updateRestaurantSelection(mAuth.getUid(), placeId, placeName);

    }

    void updateUserFavorite(boolean favorite, String placeId){
        mIsUserFavMutable.setValue(favorite);

        if (!favorite){
            //Remove from FireStore
            mFireStoreUserRepo.deleteFavoriteRestaurant(mAuth.getUid(), placeId);
        } else {
            //Add to FireStore
            mFireStoreUserRepo.addFavoriteRestaurants(mAuth.getUid(), placeId);
        }
    }

    //--------------------------------------------------------------------------------------------//
    //                                      Notifications
    //--------------------------------------------------------------------------------------------//
    private void setNotification(String restaurantName, String restaurantId){

        //DATA
        String address = "";
        if (mDetailsLiveData.getValue() != null && mDetailsLiveData.getValue().getAddress() != null){
            address = " - " + mDetailsLiveData.getValue().getAddress();
        }

        Data data = new Data.Builder()
                .putString(NotificationWorker.KEY_RESTAURANT_NAME, restaurantName)
                .putString(NotificationWorker.KEY_ADDRESS, address)
                .putString(NotificationWorker.KEY_RESTAURANT_ID, restaurantId)
                .build();

        //TIME
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();

        // Set Execution around 12:00:00 PM
        dueDate.set(Calendar.HOUR_OF_DAY, 12);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);

        if (dueDate.before(currentDate)){
            //It's 12PM past, set it for tomorrow then
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
            Log.d("debuglog", "setNotification: " + dueDate.toString());
        }

        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();

        PeriodicWorkRequest notifRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 20, TimeUnit.MINUTES)
                        .setInputData(data)
                        .setConstraints(constraints)
                        .setInitialDelay(0, TimeUnit.MINUTES)
                        //.setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                        .addTag(NOTIF_TAG)
                        .build();

        WorkManager.getInstance(mApplication.getApplicationContext()).enqueue(notifRequest);
    }

    private void disableNotification(){
        WorkManager.getInstance(mApplication.getApplicationContext()).cancelAllWorkByTag(NOTIF_TAG);

    }

}
