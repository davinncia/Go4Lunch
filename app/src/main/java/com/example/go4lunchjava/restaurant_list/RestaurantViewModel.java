package com.example.go4lunchjava.restaurant_list;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.repository.FireStoreRepository;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RestaurantViewModel extends ViewModel {

    private FireStoreRepository mFireStoreRepository;

    private MutableLiveData<List<RestaurantItem>> mRestaurantsMutableLiveData = new MutableLiveData<>();
    LiveData<List<RestaurantItem>> mRestaurantsLiveData = mRestaurantsMutableLiveData;

    private MutableLiveData<List<RestaurantItem>> mFilteredRestaurantsMutableLiveData = new MutableLiveData<>();
    LiveData<List<RestaurantItem>> mFilteredRestaurants = mFilteredRestaurantsMutableLiveData;

    public RestaurantViewModel(Application application){

        mFireStoreRepository = FireStoreRepository.getInstance();

        getRestaurantList();
    }

    private void getRestaurantList(){
        //Get cache

    }

    //Getting the details about opening hours
    private void fetchRestaurantDetails(List<RestaurantItem> restaurants){

    }

    //OPENING HOURS
    private void fetchOpeningHours(List<String> placeIds){

    }


    //WORKMATE JOINING
    private void fetchNumberOfWorkmatesInRestaurant(List<RestaurantItem> restaurants){
        Log.d("debuglog", "fetching workmates");
        //DEBUG
        List<RestaurantItem> r1 = restaurants;

        mFireStoreRepository.getAllUserDocuments().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot document : queryDocumentSnapshots){

                for (RestaurantItem restaurant : restaurants) {

                    if (Objects.requireNonNull(document.get(Workmate.FIELD_RESTAURANT_ID)).equals(restaurant.getPlaceId())
                    && !document.getId().equals(FirebaseAuth.getInstance().getUid())){
                        //We have a match !
                        Log.d("debuglog", "We have a match !");

                        restaurant.setWorkmatesJoingingNbr(restaurant.getWorkmatesJoiningNbr() + 1);
                        //TODO NINO: Update la vue dès maintenant ou attendre la nouvelle liste entière ?

                    }
                }
            }

            //TODO NINO: submitList n'actualise pas la vue...
            mRestaurantsMutableLiveData.setValue(new ArrayList<>(restaurants));

        });

    }


    void provideRestaurantList(List<RestaurantItem> restaurants){
        mRestaurantsMutableLiveData.setValue(restaurants);
        //Once we've displayed the initial list, let's get more details
        fetchNumberOfWorkmatesInRestaurant(restaurants);
    }

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

}