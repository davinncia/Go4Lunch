package com.example.go4lunchjava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.go4lunchjava.auth.AuthentificationActivity;
import com.example.go4lunchjava.map.MapFragment;
import com.example.go4lunchjava.restaurant_list.RestaurantListFragment;
import com.example.go4lunchjava.workmates_list.WorkmatesFragment;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    public static final int AUTO_COMPLETE_REQUEST_CODE = 1;

    //Drawer layout
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    //FireBase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();

    //Search
    private EditText mSearchEditText;
    private String mSearchPlaceId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startActivity(new Intent(this, RestaurantDetailsActivity.class));
        mSearchEditText = findViewById(R.id.edit_text_search);

        configureNavigationDrawer();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container_main, MapFragment.newInstance()).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_bar_main);
        bottomNavigationView.setOnNavigationItemReselectedListener(item -> { }); //Do nothing if already selected
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> updateFragment(item.getItemId()));
        

    }

    //////////////////
    ////BOTTOM BAR////
    //////////////////
    private Boolean updateFragment(Integer itemId){

        Fragment fragment;

        switch (itemId){
            case R.id.action_map_view:
                fragment = MapFragment.newInstance();
                break;
            case R.id.action_list_view:
                String specificPlace = mSearchPlaceId;
                mSearchPlaceId = null;
                fragment = RestaurantListFragment.newInstance(specificPlace);
                break;
            case R.id.action_workmates:
                fragment = WorkmatesFragment.newInstance();
                break;
            default:
                throw new IllegalArgumentException("No bottom bar match for: " + itemId);
        }

        //If a search took place, hide search EditText
        mSearchEditText.setVisibility(View.GONE);

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container_main, fragment).commit();
        return true;
    }

    //////////////////
    //////DRAWER//////
    //////////////////
    private void configureNavigationDrawer(){

        drawerLayout = findViewById(R.id.drawer_layout_main);
        navigationView = findViewById(R.id.nav_view_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);

        populateDrawerHeader();

        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.main_drawer_your_lunch:
                        Toast.makeText(MainActivity.this, "Your lunch", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.main_drawer_settings:
                        Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.main_drawer_logout:
                        logOutUser();
                        break;
                    default:
                        break;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    private void populateDrawerHeader(){

        View headerView = navigationView.getHeaderView(0);
        ImageView avatarView = headerView.findViewById(R.id.iv_avatar_nav_drawer_header);

        Uri userPhotoUri = mUser.getPhotoUrl();

        if (userPhotoUri != null){
            Glide.with(this)
                    .load(userPhotoUri)
                    .centerCrop()
                    .into(avatarView);
        }
    }

    //////////////////
    //////SEARCH//////
    //////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.menu_action_search){

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout_container_main);

            if (fragment instanceof MapFragment){
                searchPlaceOnMap();
            } else if (fragment instanceof RestaurantListFragment){
                ((RestaurantListFragment) fragment).setSearchEditText();
            } else if (fragment instanceof WorkmatesFragment){
                Toast.makeText(this, "Search workmates", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void searchPlaceOnMap(){
        List<Place.Field> fields = Collections.singletonList(Place.Field.ID);
        //Places auto complete intent
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(this);
        startActivityForResult(intent, AUTO_COMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTO_COMPLETE_REQUEST_CODE){
            if (resultCode == AutocompleteActivity.RESULT_OK){
                assert data != null;
                Place place = Autocomplete.getPlaceFromIntent(data);
                mSearchPlaceId = place.getId();
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout_container_main);
                if (fragment instanceof MapFragment){
                    ((MapFragment) fragment).searchSpecificPlace(mSearchPlaceId);
                }
            }
        }
    }

    //////////////////
    ////FIRE BASE/////
    //////////////////
    private void logOutUser(){
        mAuth.signOut();
        startActivity(new Intent(this, AuthentificationActivity.class));
    }

}
