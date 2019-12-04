package com.example.go4lunchjava.chat;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.chat.model.ChatMessage;
import com.example.go4lunchjava.repository.ChatFireStoreRepository;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatViewModel extends ViewModel {

    private ChatFireStoreRepository mChatFireStore;
    private UsersFireStoreRepository mUsersFireStore;

    private String mChatId;
    private String mCurrentUid;
    private String mUserAvatarUri;
    private String mWorkmateAvatarUri;

    //Raw messages
    private LiveData<List<ChatMessage>> rawMessagesLiveData;
    //Ui messages
    private MediatorLiveData<List<ChatMessageModelUi>> uiMessagesMediator = new MediatorLiveData<>();
    public LiveData<List<ChatMessageModelUi>> uiMessagesLiveData = uiMessagesMediator;
    //Pictures
    private LiveData<List<User>> mAllUserDocumentsLiveData;


    public ChatViewModel(ChatFireStoreRepository chatRepo, UsersFireStoreRepository userRepo) {
        mChatFireStore = chatRepo;
        mUsersFireStore = userRepo;

    }

    public void init(String currentUid, String workmateUid) {
        mCurrentUid = currentUid;

        //Assembling our chat id from the users' one
        String chatId;
        if (currentUid.compareToIgnoreCase(workmateUid) < 0) {
            chatId = currentUid + workmateUid;
        } else {
            chatId = workmateUid + currentUid;
        }
        mChatId = chatId;


        //Messages database as source
        mChatFireStore.listenToMessages(chatId);
        rawMessagesLiveData = mChatFireStore.getMessagesLiveData();

        uiMessagesMediator.addSource(rawMessagesLiveData, this::craftMessagesForView);

        //Profile pictures as source
        mUsersFireStore.fetchAllUsersDocuments();
        mAllUserDocumentsLiveData = mUsersFireStore.getAllUserLiveData();

        uiMessagesMediator.addSource(mAllUserDocumentsLiveData, users ->
                fetchPictureUris(currentUid, workmateUid, users));

    }


    private void craftMessagesForView(List<ChatMessage> rawMessages) {
        List<ChatMessageModelUi> messageUiList = new ArrayList<>();

        for (ChatMessage message : rawMessages) {

            //FIRST OF SERIE
            boolean isFirst = true;
            int i = rawMessages.indexOf(message);
            if (i > 0) {
                if (rawMessages.get(i - 1).getSenderId().equals(rawMessages.get(i).getSenderId())) {
                    isFirst = false;
                }
            }

            //PICTURE
            String pictureUri = "";

            if (mUserAvatarUri != null && mWorkmateAvatarUri != null) {
                if (message.getSenderId().equals(mCurrentUid))
                    pictureUri = mUserAvatarUri;
                else
                    pictureUri = mWorkmateAvatarUri;
            }

            //TIME
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String time = dateFormat.format(message.getCreationTimeStamp());

            ChatMessageModelUi messageUi = new ChatMessageModelUi(
                    message.getSenderId(), message.getContent(), time,
                    pictureUri, message.getSenderId().equals(mCurrentUid), isFirst);
            messageUiList.add(messageUi);
        }

        uiMessagesMediator.setValue(messageUiList);
    }


    private void fetchPictureUris(String currentUid, String workmateUid, List<User> users) {

        for (User user : users) {

            if (user.getId().equals(currentUid)) {
                mUserAvatarUri = user.getAvatar_uri();
            } else if (user.getId().equals(workmateUid)) {
                mWorkmateAvatarUri = user.getAvatar_uri();
            }
        }

        if (rawMessagesLiveData.getValue() != null) {
            //Update uiMessages with pictures
            craftMessagesForView(rawMessagesLiveData.getValue());
        }

    }


    void addMessage(String input) {
        ChatMessage message = new ChatMessage(mCurrentUid, System.currentTimeMillis(), input);
        //Add to FireStore
        mChatFireStore.addMessage(message, mChatId);
    }
}
