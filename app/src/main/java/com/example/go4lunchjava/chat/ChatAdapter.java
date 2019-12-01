package com.example.go4lunchjava.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.go4lunchjava.R;

public class ChatAdapter extends ListAdapter<ChatMessageModelUi, MessageViewHolder> {

    ChatAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chat_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        ChatMessageModelUi currentMessage = getItem(position);

        holder.updateWithMessage(currentMessage);
    }

    /////////////////
    //DIFF CALLBACK//
    /////////////////
    private static final DiffUtil.ItemCallback<ChatMessageModelUi> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ChatMessageModelUi>() {
                @Override
                public boolean areItemsTheSame(@NonNull ChatMessageModelUi oldItem, @NonNull ChatMessageModelUi newItem) {
                    return oldItem.getTime().equals(newItem.getTime());
                }

                @Override
                public boolean areContentsTheSame(@NonNull ChatMessageModelUi oldItem, @NonNull ChatMessageModelUi newItem) {
                    return oldItem.equals(newItem);
                }
            };
}
