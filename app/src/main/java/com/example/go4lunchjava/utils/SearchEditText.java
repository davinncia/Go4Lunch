package com.example.go4lunchjava.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

public class SearchEditText extends AppCompatEditText {

    private SearchTextChangedListener mTextChangedListener;

    public SearchEditText(Context context) {
        super(context);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mTextChangedListener.onTextChanged(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //setOnFocusChangeListener((view, b) -> {
        //    if (!b) setText("");
        //});
    }

    public interface SearchTextChangedListener{
        void onTextChanged(CharSequence charSequence);
    }

    public void setSearchTextChangedListener(SearchTextChangedListener textChangedListener){
        mTextChangedListener = textChangedListener;
    }
}
