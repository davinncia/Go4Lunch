package com.example.go4lunchjava.repository;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.chat.model.ChatMessage;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UsersFireStoreRepository {
    //TODO: Create interface

    private static final String USER_COLLECTION_NAME = "users";


    private static UsersFireStoreRepository sInstance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private UsersFireStoreRepository(){
        //No constructor allowed
    }

    public static UsersFireStoreRepository getInstance(){
        if (sInstance == null) {
            synchronized (UsersFireStoreRepository.class){
                if (sInstance == null){
                    sInstance = new UsersFireStoreRepository();
                }
            }
        }
        return sInstance;
    }

    //--------------------------------------------------------------------------------------------//
    //                                          C R U D
    //--------------------------------------------------------------------------------------------//

    //CREATE
    public void createUserIfNotRegistered(String uid, String userName, String avatarUri){
        db.collection(USER_COLLECTION_NAME).document(uid).get()
                .addOnCompleteListener(task -> {
            if (!Objects.requireNonNull(task.getResult()).exists()){
                createUser(uid, userName, avatarUri);
            } else {
                Log.d("debuglog", "Workmate already registered.");
            }
        });
    }

    private void createUser(String uid, String userName, String avatarUri){
        Map<String, Object> user = new HashMap<>();
        user.put(Workmate.FIELD_NAME, userName);
        user.put(Workmate.FIELD_AVATAR, avatarUri);

        db.collection(USER_COLLECTION_NAME)
                .document(uid)
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d("debuglog", "Workmate successfully registered.");
                    else Log.d("debuglog", "Workmate not added: " + task.getException());
                });
    }

    //READ
    public Task<QuerySnapshot> getAllUserDocuments(){
        return db.collection(USER_COLLECTION_NAME).get();
    }

    //UPDATE
    public void updateRestaurantSelection(String uid, String placeId, String placeName){
        Map<String, Object> restaurant = new HashMap<>();
        restaurant.put(Workmate.FIELD_RESTAURANT_ID, placeId);
        restaurant.put(Workmate.FIELD_RESTAURANT_NAME, placeName);

        db.collection(USER_COLLECTION_NAME)
                .document(uid)
                .update(restaurant)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d("debuglog", "Restaurant updated in FireStore.");
                    else Log.d("debuglog", "Error updating restaurant in ForeStore." + task.getException());
                });
    }

    public void addFavoriteRestaurants(String uid, String placeId){
        db.collection(USER_COLLECTION_NAME)
                .document(uid)
                .update(Workmate.FIELD_FAVORITE_RESTAURANTS, FieldValue.arrayUnion(placeId));
    }

    //DELETE
    public void deleteFavoriteRestaurant(String uid, String placeId){
        db.collection(USER_COLLECTION_NAME)
                .document(uid)
                .update(Workmate.FIELD_FAVORITE_RESTAURANTS, FieldValue.arrayRemove(placeId));
    }



}
