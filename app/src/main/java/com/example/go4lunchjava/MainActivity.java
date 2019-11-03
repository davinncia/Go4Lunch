package com.example.go4lunchjava;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.go4lunchjava.map.MapFragment;
import com.example.go4lunchjava.restaurants.RestaurantListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Drawer layout
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureNavigationDrawer();

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

    private void configureNavigationDrawer(){

        drawerLayout = findViewById(R.id.drawer_layout_main);
        navigationView = findViewById(R.id.nav_view_main);
        Toolbar toolbar = findViewById(R.id.main_toolbar);

        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
                        Toast.makeText(MainActivity.this, "Log out", Toast.LENGTH_SHORT).show();
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
            super.onBackPressed();
        }
    }

    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(toggle.onOptionsItemSelected(item))
            return true; //Opening drawer

        return super.onOptionsItemSelected(item);
    }
 */
}
