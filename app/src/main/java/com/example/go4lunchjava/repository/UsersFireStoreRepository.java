package com.example.go4lunchjava.repository;


import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.android.gms.tasks.Task;
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

    private static final String USER_COLLECTION_NAME = "users";

    private static UsersFireStoreRepository sInstance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Data for ViewModels to observe
    private MutableLiveData<List<User>> allUsersDocumentsMutable = new MutableLiveData<>();

    private UsersFireStoreRepository() {
        //No constructor allowed
    }

    public static UsersFireStoreRepository getInstance() {
        if (sInstance == null) {
            synchronized (UsersFireStoreRepository.class) {
                if (sInstance == null) {
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
    public void createUserIfNotRegistered(String uid, String userName, String avatarUri) {
        db.collection(USER_COLLECTION_NAME).document(uid).get()
                .addOnCompleteListener(task -> {
                    if (!Objects.requireNonNull(task.getResult()).exists()) {
                        createUser(uid, userName, avatarUri);
                    } else {
                        Log.d("debuglog", "Workmate already registered.");
                    }
                });
    }

    private void createUser(String uid, String userName, String avatarUri) {
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
    public Task<QuerySnapshot> getAllUserDocuments() {
        return db.collection(USER_COLLECTION_NAME).get();
    }

    public void fetchAllUsersDocuments() {
        db.collection(USER_COLLECTION_NAME).get().addOnSuccessListener(queryDocumentSnapshots -> {

            if (queryDocumentSnapshots == null) {
                Log.w(UsersFireStoreRepository.class.getSimpleName(), "No users found in FireStore.");
                return;
            }

            List<User> users = new ArrayList<>();
            //DEBUG
            int i = 0;
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                users.add(document.toObject(User.class));

                users.get(i).setId(document.getId()); //TODO: directly in constructor
                i++;
            }

            allUsersDocumentsMutable.setValue(users);
        });
    }

    //UPDATE
    public void updateRestaurantSelection(String uid, String placeId, String placeName) {
        Map<String, Object> restaurant = new HashMap<>();
        restaurant.put(Workmate.FIELD_RESTAURANT_ID, placeId);
        restaurant.put(Workmate.FIELD_RESTAURANT_NAME, placeName);

        db.collection(USER_COLLECTION_NAME)
                .document(uid)
                .update(restaurant)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful())
                        Log.w(UsersFireStoreRepository.class.getSimpleName(),
                                "Error updating restaurant in ForeStore." + task.getException());
                });
    }

    public void addFavoriteRestaurants(String uid, String placeId) {
        db.collection(USER_COLLECTION_NAME)
                .document(uid)
                .update(Workmate.FIELD_FAVORITE_RESTAURANTS, FieldValue.arrayUnion(placeId));
    }

    //DELETE
    public void deleteFavoriteRestaurant(String uid, String placeId) {
        db.collection(USER_COLLECTION_NAME)
                .document(uid)
                .update(Workmate.FIELD_FAVORITE_RESTAURANTS, FieldValue.arrayRemove(placeId));
    }

    //--------------------------------------------------------------------------------------------//
    //                                       G E T T E R S
    //--------------------------------------------------------------------------------------------//
    public LiveData<List<User>> getAllUserLiveData(){
        return allUsersDocumentsMutable;
    }

}
