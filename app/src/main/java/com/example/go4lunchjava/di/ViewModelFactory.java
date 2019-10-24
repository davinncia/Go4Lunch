package com.example.go4lunchjava.di;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunchjava.auth.LogInCheckViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Application mApplication;

    public ViewModelFactory(Application application){
        this.mApplication = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LogInCheckViewModel.class)) {
            return (T) new LogInCheckViewModel(mApplication);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
