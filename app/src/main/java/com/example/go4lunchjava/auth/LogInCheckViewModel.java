package com.example.go4lunchjava.auth;

import android.app.Application;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInCheckViewModel extends ViewModel {


    private Application mApplication;

    private MutableLiveData<Intent> mIntent = new MutableLiveData<>();
    public LiveData<Intent> mIntentLiveData = mIntent;

    //CONSTRUCTOR
    public LogInCheckViewModel(Application application) {
        this.mApplication = application;
    }

    void checkUserConnected(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null){
            mIntent.setValue(new Intent(mApplication, AuthentificationActivity.class));
        } else {
            mIntent.setValue(new Intent(mApplication, MainActivity.class));
        }
    }
}
