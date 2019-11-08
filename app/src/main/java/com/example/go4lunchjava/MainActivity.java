package com.example.go4lunchjava;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.go4lunchjava.auth.AuthentificationActivity;
import com.example.go4lunchjava.map.MapFragment;
import com.example.go4lunchjava.restaurant_details.RestaurantDetailsActivity;
import com.example.go4lunchjava.restaurant_list.RestaurantListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //Drawer layout
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    //FireBase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser mUser = mAuth.getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startActivity(new Intent(this, RestaurantDetailsActivity.class));

        configureNavigationDrawer();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_container_main, MapFragment.newInstance()).commit();

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

    //TODO NINO: In a specific ViewModel ?
    private void logOutUser(){
        mAuth.signOut();
        startActivity(new Intent(this, AuthentificationActivity.class));
    }

}
