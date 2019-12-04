package com.example.go4lunchjava.chat;

import android.net.Uri;

import java.util.Objects;

public class ChatMessageModelUi {

    private String mSenderId;
    private String mContent;
    private String mTime;
    private String mPictureUri;
    private boolean mCurrentUserIsSender; //Use to set orientation of text on screen
    private boolean mIsFirstOfSerie; //Use to display profile picture & time or not

    ChatMessageModelUi(String senderId, String content, String time, String pictureUri, boolean currentUserIsSender, boolean isFirstOfSerie) {
        this.mSenderId = senderId;
        this.mContent = content;
        this.mTime = time;
        this.mPictureUri = pictureUri;
        this.mCurrentUserIsSender = currentUserIsSender;
        this.mIsFirstOfSerie = isFirstOfSerie;
    }

    public String getSenderId() {
        return mSenderId;
    }

    public String getContent() {
        return mContent;
    }

    String getTime() {
        return mTime;
    }

    public String getPictureUri() {
        return mPictureUri;
    }

    boolean currentUserIsSender() {
        return mCurrentUserIsSender;
    }

    boolean isFirstOfSerie() {
        return mIsFirstOfSerie;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessageModelUi that = (ChatMessageModelUi) o;
        return mCurrentUserIsSender == that.mCurrentUserIsSender &&
                mSenderId.equals(that.mSenderId) &&
                Objects.equals(mContent, that.mContent) &&
                Objects.equals(mPictureUri, that.mPictureUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mSenderId, mContent, mTime, mPictureUri, mCurrentUserIsSender);
    }
}
