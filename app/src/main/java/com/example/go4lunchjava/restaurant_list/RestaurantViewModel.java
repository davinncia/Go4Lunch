package com.example.go4lunchjava.restaurant_list;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class RestaurantViewModel extends ViewModel {

    private MutableLiveData<List<RestaurantItem>> mFilteredRestaurantsMutableLiveData = new MutableLiveData<>();
    LiveData<List<RestaurantItem>> mFilteredRestaurants = mFilteredRestaurantsMutableLiveData;

    public RestaurantViewModel(Application application){

    }

    //TODO: Unit Test
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