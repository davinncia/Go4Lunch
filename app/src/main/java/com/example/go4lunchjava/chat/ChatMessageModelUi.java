package com.example.go4lunchjava.chat;

import android.net.Uri;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageModelUi that = (ChatMessageModelUi) o;
        return mCurrentUserIsSender == that.mCurrentUserIsSender &&
                mSenderId.equals(that.mSenderId) &&
                Objects.equals(mContent, that.mContent) &&
                Objects.equals(mSenderPictureUri, that.mSenderPictureUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mSenderId, mContent, mTime, mSenderPictureUri, mCurrentUserIsSender);
    }
}
