package com.example.go4lunchjava.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.chat.model.ChatMessage;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ChatFireStoreRepository {

    private static final String CHAT_COLLECTION_NAME = "chat";
    private static final String MESSAGES_COLLECTION_NAME = "messages";
    private static final String TAG = UsersFireStoreRepository.class.getSimpleName();

    private static ChatFireStoreRepository sInstance;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Data for ViewModel
    private MutableLiveData<List<ChatMessage>> messagesMutable = new MutableLiveData<>();


    //SINGLETON PATTERN
    private ChatFireStoreRepository() {}

    public static ChatFireStoreRepository getInstance(){
        if (sInstance == null){
            synchronized (ChatFireStoreRepository.class){
                if (sInstance == null) {
                    sInstance = new ChatFireStoreRepository();
                }
            }
        }
        return sInstance;
    }

    //--------------------------------------------------------------------------------------------//
    //                                          C R U D
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
        });
    }

    //CREATE
    public void addMessage(ChatMessage message, String chatId){
        db.collection(CHAT_COLLECTION_NAME).document(chatId).collection(MESSAGES_COLLECTION_NAME)
                .document().set(message);
    }

    //--------------------------------------------------------------------------------------------//
    //                                       G E T T E R S
    //--------------------------------------------------------------------------------------------//
    public LiveData<List<ChatMessage>> getMessagesLiveData(){
        return messagesMutable;
    }
}
