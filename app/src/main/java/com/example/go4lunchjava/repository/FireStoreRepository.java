package com.example.go4lunchjava.repository;


import android.net.Uri;
import android.util.Log;

import com.example.go4lunchjava.auth.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FireStoreRepository {

    //TODO NINO: architecture, faut-il créer une interface + class service ?

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
    public void createUserIfNotRegistered(String email, String userName, String avatarUri){
        db.collection(USER_COLLECTION_NAME).document(email).get()
                .addOnCompleteListener(task -> {
            if (!Objects.requireNonNull(task.getResult()).exists()){
                createUser(email, userName, avatarUri);
            } else {
                Log.d("debuglog", "User already registered.");
            }
        });
    }

    private void createUser(String email, String userName, String avatarUri){
        Map<String, Object> user = new HashMap<>();
        user.put(User.FIELD_NAME, userName);
        user.put(User.FIELD_AVATAR, avatarUri);

        db.collection(USER_COLLECTION_NAME)
                .document(email)
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Log.d("debuglog", "User successfully registered.");
                    else Log.d("debuglog", "User not added: " + task.getException());
                });
    }


}
