package com.example.go4lunchjava.restaurant_list;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.di.ViewModelFactory;
import com.example.go4lunchjava.map.MapViewModel;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.restaurant_details.RestaurantDetailsActivity;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;
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

        LinearLayout emptyView = rootView.findViewById(R.id.linearLayout_empty_list_restaurant);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_restaurants);
        initRecyclerView();

        //Get data from view model
        ViewModelFactory viewModelFactory = new ViewModelFactory(Objects.requireNonNull(getActivity()).getApplication());
        //Shared ViewModel
        MapViewModel mapViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(MapViewModel.class);

        mapViewModel.mRestaurantsLiveData.observe(this, restaurantItems -> {
            Log.d("debuglog", "RestaurantListView update");
            if (restaurantItems == null || restaurantItems.size() < 1)
                emptyView.setVisibility(View.VISIBLE);
            else {
                emptyView.setVisibility(View.GONE);
                mAdapter.populateRecyclerView(restaurantItems); //Display data on screen
            }
        });

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

        startActivity(RestaurantDetailsActivity.newIntent(this.getContext(), restaurant.getPlaceId()));
        Toast.makeText(getContext(), restaurant.getName(), Toast.LENGTH_SHORT).show();
    }
}
