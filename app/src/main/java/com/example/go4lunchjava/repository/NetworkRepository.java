package com.example.go4lunchjava.repository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class NetworkRepository {

    private static NetworkRepository sIntance;

    private MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();

    private NetworkRepository(Context context) {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkReceiver, filter);
    }

    public static NetworkRepository getInstance(Context context) {
        if (sIntance == null){
            synchronized (NetworkRepository.class){
                if (sIntance == null){
                    return new NetworkRepository(context);
                }
            }
        }
        return sIntance;
    }


    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras() != null){
                    ConnectivityManager connectivityManager =
                            (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    assert connectivityManager != null;
                    NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
                    //NetworkInfo activeNetwork = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK);

                    boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                    mIsConnected.postValue(isConnected);
                }
            }
    };


    public LiveData<Boolean> getNetworkStatusLiveData() {
        return mIsConnected;
    }
}
