package com.example.go4lunchjava.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.di.ViewModelFactory;

public class ChatActivity extends AppCompatActivity {

    //DATA
    public static final String KEY_USER_ID = "key_user_id";
    public static final String KEY_WORKMATE_ID = "key_workmate_id";

    private int messageNbr = 0;

    private ChatViewModel mViewModel;
    private ChatAdapter mAdapter;
    private RecyclerView mRecyclerView;

    public static Intent navigate(Context context, String currentUid, String workmateUid){

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(KEY_USER_ID, currentUid);
        intent.putExtra(KEY_WORKMATE_ID, workmateUid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Button sendButton = findViewById(R.id.btn_chat_send);
        EditText inputEditText = findViewById(R.id.tv_input_chat_message);

        ViewModelFactory factory = ViewModelFactory.getInstance(getApplication());
        mViewModel = ViewModelProviders.of(this, factory).get(ChatViewModel.class);

        //Retrieving the chat ids
        String currentUid = getIntent().getStringExtra(KEY_USER_ID);
        String workmateUid = getIntent().getStringExtra(KEY_WORKMATE_ID);

        if (currentUid != null && workmateUid != null) mViewModel.init(currentUid, workmateUid);

        initRecyclerView();


        mViewModel.uiMessagesLiveData.observe(this, messages -> {

            if (messageNbr == messages.size() +1)
                //Just one message has been added
                mAdapter.notifyItemInserted(messages.size() - 1);
            else
                mAdapter.submitList(messages);

            messageNbr = messages.size();

        });


        sendButton.setOnClickListener(view -> {

            String text = inputEditText.getText().toString().trim();
            if (!text.isEmpty()){
                inputEditText.setText("");
                mViewModel.addMessage(text);
            }

            hideSoftKeyboard();

        });
    }

    private void initRecyclerView(){

        mRecyclerView = findViewById(R.id.recycler_view_chat);
        mAdapter = new ChatAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);

    }

    private void hideSoftKeyboard(){
        View view = this.getCurrentFocus();

        if (view!= null){
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
