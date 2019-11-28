package com.example.go4lunchjava.repository;


import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.chat.ChatMessageModelUi;
import com.example.go4lunchjava.chat.model.ChatMessage;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FireStoreRepository {
    //TODO: Create interface

    private static final String USER_COLLECTION_NAME = "users";

    private static final String CHAT_COLLECTION_NAME = "chat";
    private static final String MESSAGES_COLLECTION_NAME = "messages";

    private static final String TAG = FireStoreRepository.class.getSimpleName();

    private static FireStoreRepository sInstance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private MutableLiveData<List<ChatMessage>> messagesMutable = new MutableLiveData<>();
    public LiveData<List<ChatMessage>> messagesLiveData = messagesMutable;

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

    //--------------------------------------------------------------------------------------------//
    //                                         Restaurants
    //--------------------------------------------------------------------------------------------//
    //GET
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

    //--------------------------------------------------------------------------------------------//
    //                                          Chat
    //--------------------------------------------------------------------------------------------//
    //GET
    public void listenToMessages(String chatId){
        db.collection(CHAT_COLLECTION_NAME).document(chatId).collection(MESSAGES_COLLECTION_NAME)
                .orderBy("creationTimeStamp").addSnapshotListener((queryDocumentSnapshots, e) -> {

                    if (queryDocumentSnapshots == null || e != null){
                        Log.w(TAG, "Listen messages failed.", e);
                        return;
                    }

                    List<ChatMessage> messages = new ArrayList<>();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                        messages.add(document.toObject(ChatMessage.class));
                    }

                    messagesMutable.setValue(messages);
                    Log.d("debuglog", "Fetched messages: " + messages.size());
                });
    }

    //CREATE
    public void addMessage(ChatMessage message, String chatId){
        db.collection(CHAT_COLLECTION_NAME).document(chatId).collection(MESSAGES_COLLECTION_NAME)
                .document().set(message);
    }

}
