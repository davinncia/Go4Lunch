package com.example.go4lunchjava.restaurant_list;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.example.go4lunchjava.repository.LocationRepository;
import com.example.go4lunchjava.utils.RestaurantDataFormat;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.List;

public class RestaurantViewModel extends ViewModel {

    private LatLng mCurrentLatLng;

    public RestaurantViewModel(Application application){



    }

}
