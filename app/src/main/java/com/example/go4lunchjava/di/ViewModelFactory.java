package com.example.go4lunchjava.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunchjava.auth.LogInCheckViewModel;
import com.example.go4lunchjava.map.MapViewModel;
import com.example.go4lunchjava.restaurant_details.RestaurantDetailsViewModel;
import com.example.go4lunchjava.restaurant_list.RestaurantViewModel;
import com.example.go4lunchjava.workmates_list.WorkmateAdapter;
import com.example.go4lunchjava.workmates_list.WorkmateViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Application mApplication;

    //TODO: singleton pattern
    public ViewModelFactory(Application application){
        mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LogInCheckViewModel.class)) {
            return (T) new LogInCheckViewModel(mApplication);
        } else if (modelClass.isAssignableFrom(MapViewModel.class)){
            return (T) new MapViewModel(mApplication);
        } else if (modelClass.isAssignableFrom(RestaurantViewModel.class)){
            return (T) new RestaurantViewModel(mApplication);
        } else if (modelClass.isAssignableFrom(RestaurantDetailsViewModel.class)){
            return (T) new RestaurantDetailsViewModel();
        } else if (modelClass.isAssignableFrom(WorkmateViewModel.class)){
            return (T) new WorkmateViewModel();
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
