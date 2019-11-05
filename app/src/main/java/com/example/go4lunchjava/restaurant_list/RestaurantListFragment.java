package com.example.go4lunchjava.restaurant_list;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.di.ViewModelFactory;
import com.example.go4lunchjava.restaurant_details.RestaurantDetails;

import java.util.Objects;


public class RestaurantListFragment extends Fragment implements RestaurantAdapter.OnRestaurantClickListener {

    //Recycler View
    private RecyclerView mRecyclerView;
    private RestaurantAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    public RestaurantListFragment() {
        // Required empty public constructor
    }

    public static RestaurantListFragment newInstance() {
        return new RestaurantListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        //TODO: empty view

        mRecyclerView = rootView.findViewById(R.id.recycler_view_restaurants);
        initRecyclerView();

        //Get data from view model
        ViewModelFactory viewModelFactory = new ViewModelFactory(Objects.requireNonNull(getActivity()).getApplication());
        RestaurantViewModel restaurantViewModel = ViewModelProviders.of(this, viewModelFactory).get(RestaurantViewModel.class);

        restaurantViewModel.restaurantsLiveData.observe(this, restaurantItems -> {
            Log.d("debuglog", "RestaurantListView update");
            mAdapter.populateRecyclerView(restaurantItems);}); //Display data on screen


        return rootView;
    }

    private void initRecyclerView(){

        // Improve performance if layout size fixed
        mRecyclerView.setHasFixedSize(true);

        //Divider deco
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Objects.requireNonNull(this.getContext()), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RestaurantAdapter();
        mAdapter.setOnRestaurantClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }


    @Override
    public void onRestaurantClick(RestaurantItem restaurant) {

        startActivity(RestaurantDetails.newIntent(this.getContext(), restaurant.getPlaceId()));
        Toast.makeText(getContext(), restaurant.getName(), Toast.LENGTH_SHORT).show();
    }
}
