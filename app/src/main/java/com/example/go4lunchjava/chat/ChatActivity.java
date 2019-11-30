package com.example.go4lunchjava.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.di.ViewModelFactory;

public class ChatActivity extends AppCompatActivity {

    //DATA
    public static final String KEY_CHAT_ID = "key_chat_id";


    private ChatViewModel mViewModel;
    private ChatAdapter mAdapter;

    public static Intent navigate(Context context, String chatId){

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_CHAT_ID, chatId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Button sendButton = findViewById(R.id.btn_chat_send);
        EditText inputEditText = findViewById(R.id.tv_input_chat_message);

        ViewModelFactory factory = new ViewModelFactory(getApplication());
        mViewModel = ViewModelProviders.of(this, factory).get(ChatViewModel.class);

        //Retrieving the Chat id
        String chatId = getIntent().getStringExtra(KEY_CHAT_ID);
        mViewModel.startListeningToChat(chatId);

        initRecyclerView();

        mViewModel.messagesLiveData.observe(this,
                messages -> {

                    Log.d("debuglog", "Observing some messages: " + messages.size());
                    mAdapter.submitList(messages);
                });


        sendButton.setOnClickListener(view -> {
            String text = inputEditText.getText().toString().trim();
            if (!text.isEmpty()){
                inputEditText.setText("");
                mViewModel.addMessage(text);
            }
        });
    }

    private void initRecyclerView(){

        RecyclerView mRecyclerView = findViewById(R.id.recycler_view_chat);
        mAdapter = new ChatAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

    }
}
