package com.example.go4lunchjava.restaurant_details;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.go4lunchjava.R;

public class RestaurantDetails extends AppCompatActivity {

    public static final String RESTAURANT_ID_KEY = "restaurant_id";

    public static Intent newIntent(Context context, String restaurantId){
        Intent intent = new Intent(context, RestaurantDetails.class);
        intent.putExtra(RESTAURANT_ID_KEY, restaurantId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        String restaurantId = getIntent().getStringExtra(RESTAURANT_ID_KEY);

        TextView idTv = findViewById(R.id.tv_restaurant_id);

        idTv.setText(restaurantId);
    }
}
