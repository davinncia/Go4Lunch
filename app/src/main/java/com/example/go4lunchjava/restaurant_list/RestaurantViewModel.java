package com.example.go4lunchjava.restaurant_list;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import java.util.ArrayList;
import java.util.List;

public class RestaurantViewModel extends ViewModel {

    private MutableLiveData<List<RestaurantItem>> mRestaurantsMutableLiveData = new MutableLiveData<>();
    private LiveData<List<RestaurantItem>> mRestaurantsLiveData = mRestaurantsMutableLiveData;

    private MutableLiveData<List<RestaurantItem>> mFilteredRestaurantsMutableLiveData = new MutableLiveData<>();
    LiveData<List<RestaurantItem>> mFilteredRestaurants = mFilteredRestaurantsMutableLiveData;

    public RestaurantViewModel(Application application){

        getRestaurantList();
    }

    private void getRestaurantList(){
        //Get cache

    }

    //Getting the details about opening hours
    private void fetchRestaurantDetails(List<RestaurantItem> restaurants){

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