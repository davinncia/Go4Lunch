package com.example.go4lunchjava.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunchjava.auth.LogInCheckViewModel;
import com.example.go4lunchjava.chat.ChatViewModel;
import com.example.go4lunchjava.map.MapViewModel;
import com.example.go4lunchjava.repository.ChatFireStoreRepository;
import com.example.go4lunchjava.repository.LocationRepository;
import com.example.go4lunchjava.repository.NetworkRepository;
import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.restaurant_details.RestaurantDetailsViewModel;
import com.example.go4lunchjava.restaurant_list.RestaurantListViewModel;
import com.example.go4lunchjava.workmates_list.WorkmateViewModel;
import com.google.firebase.auth.FirebaseAuth;

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
            return (T) new MapViewModel(mApplication, LocationRepository.getInstance(mApplication),
                    PlacesApiRepository.getInstance(), UsersFireStoreRepository.getInstance(), NetworkRepository.getInstance(mApplication));
        } else if (modelClass.isAssignableFrom(RestaurantListViewModel.class)){
            return (T) new RestaurantListViewModel(mApplication, UsersFireStoreRepository.getInstance(),
                    PlacesApiRepository.getInstance(), FirebaseAuth.getInstance(), LocationRepository.getInstance(mApplication));
        } else if (modelClass.isAssignableFrom(RestaurantDetailsViewModel.class)){
            return (T) new RestaurantDetailsViewModel(mApplication, PlacesApiRepository.getInstance(),
                    UsersFireStoreRepository.getInstance(), FirebaseAuth.getInstance());
        } else if (modelClass.isAssignableFrom(WorkmateViewModel.class)){
            return (T) new WorkmateViewModel(UsersFireStoreRepository.getInstance(), FirebaseAuth.getInstance());
        } else if (modelClass.isAssignableFrom(ChatViewModel.class)){
            return (T) new ChatViewModel(ChatFireStoreRepository.getInstance(), UsersFireStoreRepository.getInstance());
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
