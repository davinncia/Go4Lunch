package com.example.go4lunchjava.chat;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.chat.model.ChatMessage;
import com.example.go4lunchjava.repository.ChatFireStoreRepository;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatViewModel extends ViewModel {

    private ChatFireStoreRepository mChatFireStore;
    private UsersFireStoreRepository mUsersFireStore;

    private String mChatId;
    private String mCurrentUid;
    private Uri mUserAvatar;
    private Uri mWorkmateAvatar;

    //Raw messages
    private LiveData<List<ChatMessage>> rawMessagesLiveData;
    //Ui messages
    private MediatorLiveData<List<ChatMessageModelUi>> uiMessagesMediator = new MediatorLiveData<>();
    LiveData<List<ChatMessageModelUi>> uiMessagesLiveData = uiMessagesMediator;
    //Pictures
    private MutableLiveData<Boolean> picturesProvided = new MutableLiveData<>(false);

    public ChatViewModel() {
        mChatFireStore = ChatFireStoreRepository.getInstance();
        mUsersFireStore = UsersFireStoreRepository.getInstance();
    }

    void init(String currentUid, String workmateUid){
        mCurrentUid = currentUid;

        //Assembling our chat id from the users' one
        String chatId;
        if (currentUid.compareToIgnoreCase(workmateUid) < 0) {
            chatId = currentUid + workmateUid;
        } else {
            chatId = workmateUid + currentUid;
        }
        mChatId = chatId;

        fetchPictureUris(currentUid, workmateUid);
        mChatFireStore.listenToMessages(chatId);
        rawMessagesLiveData = mChatFireStore.messagesLiveData;

        //Messages database as source
        uiMessagesMediator.addSource(rawMessagesLiveData, this::craftMessagesForView);

        //Profile pictures as source
        uiMessagesMediator.addSource(picturesProvided, stringUriMap -> {
            if (rawMessagesLiveData.getValue() != null){
                //Update ui list now that pictures have been provided
                craftMessagesForView(rawMessagesLiveData.getValue());
            }
        });

    }

    private void craftMessagesForView(List<ChatMessage> rawMessages){
        List<ChatMessageModelUi> messageUiList = new ArrayList<>();

        for (ChatMessage message : rawMessages) {

            //FIRST OF SERIE
            boolean isFirst = true;
            int i = rawMessages.indexOf(message);
            if (i > 0){
                if (rawMessages.get(i - 1).getSenderId().equals(rawMessages.get(i).getSenderId())){
                    isFirst = false;
                }
            }

            //PICTURE
            Uri pictureUri = Uri.parse("");

            if (mUserAvatar != null && mWorkmateAvatar != null) {
                if (message.getSenderId().equals(mCurrentUid))
                    pictureUri = mUserAvatar;
                else
                    pictureUri = mWorkmateAvatar;
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

    private void fetchPictureUris(String currentUid, String workmateUid) {

        mUsersFireStore.getAllUserDocuments().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                if (document.getId().equals(currentUid)) {
                    mUserAvatar = Uri.parse(String.valueOf(document.get(Workmate.FIELD_AVATAR)));
                } else if (document.getId().equals(workmateUid)) {
                    mWorkmateAvatar = Uri.parse(String.valueOf(document.get(Workmate.FIELD_AVATAR)));
                }
            }
            picturesProvided.setValue(true);

        });

    }

    /*
    void init(String currentUid, String workmateUid) {
        mCurrentUid = currentUid;

        //Assembling our chat id from the users' one
        String chatId;
        if (currentUid.compareToIgnoreCase(workmateUid) < 0) {
            chatId = currentUid + workmateUid;
        } else {
            chatId = workmateUid + currentUid;
        }
        mChatId = chatId;

        fetchPictureUris(currentUid, workmateUid);
    }


    private void startListeningToChat(String chatId){
        //Start listening to FireBase real time database
        mChatFireStore.listenToMessages(chatId);
        messagesLiveData = Transformations.map(mChatFireStore.messagesLiveData, messages -> {
            List<ChatMessageModelUi> messageUiList = new ArrayList<>();

            for (ChatMessage message : messages) {

                //PICTURE
                Uri pictureUri = Uri.parse("");

                if (mUserAvatar != null && mWorkmateAvatar != null) {
                    if (message.getSenderId().equals(mCurrentUid))
                        pictureUri = mUserAvatar;
                    else
                        pictureUri = mWorkmateAvatar;
                }

                ChatMessageModelUi messageUi = new ChatMessageModelUi(
                        message.getSenderId(), message.getContent(), "10h00",
                        pictureUri, message.getSenderId().equals(mCurrentUid));
                messageUiList.add(messageUi);
            }

            return messageUiList;
        });
    }

    private void fetchPictureUris(String currentUid, String workmateUid) {

        mUsersFireStore.getAllUserDocuments().addOnSuccessListener(queryDocumentSnapshots -> {

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                if (document.getId().equals(currentUid)) {
                    mUserAvatar = Uri.parse(String.valueOf(document.get(Workmate.FIELD_AVATAR)));
                } else if (document.getId().equals(workmateUid)) {
                    mWorkmateAvatar = Uri.parse(String.valueOf(document.get(Workmate.FIELD_AVATAR)));
                }
            }

            //TODO: Better to use a Mediator live data instead of sequencing ?
            //TODO: rawMessageLive = repository; liveMessageLive = Mediator.

            if (messagesLiveData.getValue() != null && messagesLiveData.getValue().size() > 0) {
                //List has already been populated, let's update it with the pictures then
                updateListWithPictures();
            }


        });
        startListeningToChat(mChatId);
    }

    private void updateListWithPictures() {

        List<ChatMessageModelUi> newList = new ArrayList<>();
        List<ChatMessageModelUi> oldList = messagesLiveData.getValue();
        if (oldList == null) return;

        for (ChatMessageModelUi message : oldList) {
            //PICTURE
            Uri pictureUri;

            if (message.getSenderId().equals(mCurrentUid))
                pictureUri = mUserAvatar;
            else
                pictureUri = mWorkmateAvatar;

            ChatMessageModelUi newMessage = new ChatMessageModelUi(message.getSenderId(), message.getContent(),
                    message.getTime(), pictureUri, message.currentUserIsSender());
            newList.add(newMessage);
        }

        messagesMutable.setValue(newList);
    }
    */

    void addMessage(String input) {
        ChatMessage message = new ChatMessage(mCurrentUid, System.currentTimeMillis(), input);
        //Add to FireStore
        mChatFireStore.addMessage(message, mChatId);
    }
}
