package com.example.go4lunchjava.chat;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;

public class ChatMessageModelUi {

    private String mSenderId;
    private String mContent;
    private String mTime;
    private URL mSenderPictureUri;
    private boolean mCurrentUserIsSender;

    public ChatMessageModelUi(String senderId, String content, String time, boolean currentUserIsSender) {
        this.mSenderId = senderId;
        this.mContent = content;
        this.mTime = time;
        this.mCurrentUserIsSender = currentUserIsSender;
    }

    public String getSenderId() {
        return mSenderId;
    }

    public String getContent() {
        return mContent;
    }

    public String getTime() {
        return mTime;
    }

    public URL getSenderPictureUri() {
        //return mSenderPictureUri;
        try {
            return new URL("http://i43.servimg.com/u/f43/11/73/45/92/ane-110.jpg");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public boolean currentUserIsSender() {
        return mCurrentUserIsSender;
    }
}
