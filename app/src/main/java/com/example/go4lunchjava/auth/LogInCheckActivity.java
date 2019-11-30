package com.example.go4lunchjava.auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import com.example.go4lunchjava.di.ViewModelFactory;

public class LogInCheckActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelFactory viewModelFactory = new ViewModelFactory(getApplication());
        LogInCheckViewModel logInVIewModel = ViewModelProviders.of(this, viewModelFactory).get(LogInCheckViewModel.class);
        logInVIewModel.mIntentLiveData.observe(this, intent -> startActivity(intent));

        logInVIewModel.checkUserConnected();

    }
}
