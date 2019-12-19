package com.example.go4lunchjava.settings;

import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.repository.SharedPrefRepository;

public class SettingsViewModel extends ViewModel {

    private SharedPrefRepository mSharedPrefRepo;

    public SettingsViewModel(SharedPrefRepository sharedPrefRepo){
        this.mSharedPrefRepo = sharedPrefRepo;
    }

    public boolean getNotifPref(){
        return mSharedPrefRepo.getNotifPref();
    }

    public int getRadiusPref(){
        return mSharedPrefRepo.getRadiusMetersPref() / 1000;
    }

    public void setNotif(boolean enabled){
        mSharedPrefRepo.setNotifPref(enabled);
    }

    public void setRadius(int radius){
        mSharedPrefRepo.setRadiusInMetters(radius * 1000);
    }
}
