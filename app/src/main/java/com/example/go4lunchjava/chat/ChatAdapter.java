package com.example.go4lunchjava.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunchjava.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<MessageViewHolder> {

    private List<ChatMessageModelUi> mChatMessages;

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chat_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        ChatMessageModelUi currentMessage = mChatMessages.get(position);

        String previousSender = "";
        if (position > 0) {
            previousSender = mChatMessages.get(position - 1).getSenderId();
        }

        holder.updateWithMessage(currentMessage, previousSender);
    }

    @Override
    public int getItemCount() {
        return mChatMessages != null ? mChatMessages.size() : 0;
    }

    public void setData(List<ChatMessageModelUi> messages){
        this.mChatMessages = messages;
        this.notifyDataSetChanged();
    }

    public void addMessage(ChatMessageModelUi message){
        mChatMessages.add(message);
        this.notifyItemInserted(mChatMessages.size());
    }
}
