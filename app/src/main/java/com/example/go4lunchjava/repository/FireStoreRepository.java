package com.example.go4lunchjava.repository;


import android.util.Log;

import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FireStoreRepository {
    //TODO: Create interface

    public static final String USER_COLLECTION_NAME = "users";

    private static FireStoreRepository sInstance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FireStoreRepository(){
        //No constructor allowed
    }

    public static FireStoreRepository getInstance(){
        if (sInstance == null) {
            synchronized (FireStoreRepository.class){
                if (sInstance == null){
                    sInstance = new FireStoreRepository();
                }
            }
        }
        return sInstance;
    }

    //GET
    public Task<DocumentSnapshot> getUser(String userId){
        return db.collection(USER_COLLECTION_NAME).document(userId).get();
    }

    public Task<QuerySnapshot> getAllUserDocuments(){
        return db.collection(USER_COLLECTION_NAME).get();
    }

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

}
