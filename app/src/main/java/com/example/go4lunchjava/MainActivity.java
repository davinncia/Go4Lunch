package com.example.go4lunchjava;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.go4lunchjava.map.MapFragment;
import com.example.go4lunchjava.restaurants.RestaurantListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container_main, RestaurantListFragment.newInstance()).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar_main);
        bottomNavigationView.setOnNavigationItemReselectedListener(item -> { }); //Do nothing if already selected
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> updateFragment(item.getItemId()));

    }


    private Boolean updateFragment(Integer itemId){

        Fragment fragment;

        switch (itemId){
            case R.id.action_map_view:
                fragment = MapFragment.newInstance();
                break;
            case R.id.action_list_view:
                fragment = RestaurantListFragment.newInstance();
                break;
            case R.id.action_workmates:
                fragment = WorkmatesFragment.newInstance();
                break;
            default:
                throw new IllegalArgumentException("No bottom bar match for: " + itemId);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container_main, fragment).commit();
        return true;
    }
}
