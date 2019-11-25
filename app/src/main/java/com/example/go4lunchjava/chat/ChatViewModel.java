package com.example.go4lunchjava.chat;

import android.util.Log;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.chat.model.ChatMessage;
import com.example.go4lunchjava.repository.FireStoreRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private FireStoreRepository mFireStore;

    private String mCurrentUid;
    MutableLiveData<String> mChatId = new MutableLiveData<>();

    private MutableLiveData<List<ChatMessageModelUi>> messagesMutable = new MutableLiveData<>();
    LiveData<List<ChatMessageModelUi>> messagesLiveData = messagesMutable;

    public ChatViewModel(){

        mFireStore = FireStoreRepository.getInstance();
        mCurrentUid = FirebaseAuth.getInstance().getUid();

    }

    void startListeningToChat(String chatId){
        mChatId.setValue(chatId);

        //Start listening to FireBase real time database
        mFireStore.listenToMessages(chatId);
        messagesLiveData = Transformations.map(mFireStore.messagesLiveData, messages -> {
            List<ChatMessageModelUi> messageUiList = new ArrayList<>();

            for (ChatMessage message : messages){
                ChatMessageModelUi messageUi = new ChatMessageModelUi(
                        message.getSenderId(), message.getContent(), "10h00",
                        message.getSenderId().equals(mCurrentUid));
                messageUiList.add(messageUi);
            }

            Log.d("debuglog", "LiveData update");

            return messageUiList;
        });
    }

    void addMessage(String input){
        ChatMessage message = new ChatMessage(mCurrentUid, System.currentTimeMillis(), input);
        //Add to FireStore
        mFireStore.addMessage(message, mChatId.getValue());
    }
}
