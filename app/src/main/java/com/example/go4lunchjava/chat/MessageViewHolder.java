package com.example.go4lunchjava.chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunchjava.R;

public class MessageViewHolder extends RecyclerView.ViewHolder {

    //PROFILE CONTAINER
    private LinearLayout profileContainer;
    private ImageView profilePicture;

    //MESSAGE CONTAINER
    private RelativeLayout messageContainer;
    private LinearLayout textMessageContainer;
    private TextView contentView;
    private TextView hourView;

    MessageViewHolder(@NonNull View itemView) {
        super(itemView);

        profileContainer = itemView.findViewById(R.id.chat_item_profile_container);
        profilePicture = itemView.findViewById(R.id.iv_chat_item_profile);

        messageContainer = itemView.findViewById(R.id.chat_item_message_container);
        textMessageContainer = itemView.findViewById(R.id.chat_item_text_message_container);

        contentView = itemView.findViewById(R.id.tv_chat_item_message_container_text_message_container);
        hourView = itemView.findViewById(R.id.tv_chat_item_message_container_hour);

    }

    void updateWithMessage(ChatMessageModelUi message){

        if (message.isFirstOfSerie()) {
            profilePicture.setVisibility(View.VISIBLE);
            hourView.setVisibility(View.VISIBLE);
            Glide.with(profilePicture.getContext()).load(message.getPictureUri()).circleCrop().into(profilePicture);
            hourView.setText(message.getTime());
        } else {
            profilePicture.setVisibility(View.INVISIBLE);
            hourView.setVisibility(View.GONE);
        }

        contentView.setText(message.getContent());


        updateDesignGivenUser(message.currentUserIsSender());
    }

    private void updateDesignGivenUser(boolean isCurrentUser){

        contentView.setTextAlignment(isCurrentUser ? View.TEXT_ALIGNMENT_TEXT_START :
                View.TEXT_ALIGNMENT_TEXT_END);

        // PROFILE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutHeader.addRule(isCurrentUser ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        this.profileContainer.setLayoutParams(paramsLayoutHeader);

        // MESSAGE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutContent.addRule(isCurrentUser ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.chat_item_profile_container);
        this.messageContainer.setLayoutParams(paramsLayoutContent);

        int backgroundId = isCurrentUser ? R.drawable.ic_chat_message_background_sender : R.drawable.ic_chat_message_backgroung_receiver;
        this.textMessageContainer.setBackground(textMessageContainer.getResources().getDrawable(backgroundId));
    }
}
