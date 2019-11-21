package com.example.go4lunchjava.restaurant_list;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
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
import com.example.go4lunchjava.utils.SearchEditText;
import com.example.go4lunchjava.di.ViewModelFactory;
import com.example.go4lunchjava.map.MapViewModel;
import com.example.go4lunchjava.restaurant_details.RestaurantDetailsActivity;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.go4lunchjava.MainActivity.AUTO_COMPLETE_REQUEST_CODE;


public class RestaurantListFragment extends Fragment implements RestaurantAdapter.OnRestaurantClickListener {

    //Recycler View
    private RecyclerView mRecyclerView;
    private RestaurantAdapter mAdapter;

    //SEARCH
    private static final String RESTAURANT_LIST_FRAGMENT_ID_KEY = "place_id_key_list_fragment";
    private List<RestaurantItem> mRestaurantItems = new ArrayList<>();
    private SearchEditText mSearchEditText;


    public RestaurantListFragment() {
        // Required empty public constructor
    }

    public static RestaurantListFragment newInstance(String placeId) {
        RestaurantListFragment fragment = new RestaurantListFragment();
        Bundle args = new Bundle();
        args.putString(RESTAURANT_LIST_FRAGMENT_ID_KEY, placeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

        mSearchEditText = getActivity().findViewById(R.id.edit_text_search);
        ContentLoadingProgressBar progressBar = rootView.findViewById(R.id.progress_bar_restaurant_list);
        LinearLayout emptyView = rootView.findViewById(R.id.linearLayout_empty_list_restaurant);
        mRecyclerView = rootView.findViewById(R.id.recycler_view_restaurants);

        initRecyclerView();

        //Get our view model
        ViewModelFactory viewModelFactory = new ViewModelFactory(Objects.requireNonNull(getActivity()).getApplication());
        RestaurantViewModel restaurantViewModel = ViewModelProviders.of(this, viewModelFactory).get(RestaurantViewModel.class);

        //INIT
        restaurantViewModel.init(getArguments().getString(RESTAURANT_LIST_FRAGMENT_ID_KEY));

        //UPDATING DATA
        restaurantViewModel.mRestaurantsLiveData.observe(this, restaurantItems -> {

            mRestaurantItems = restaurantItems; //SEARCH
            progressBar.setVisibility(View.GONE);

            if (restaurantItems == null || restaurantItems.size() < 1)
                emptyView.setVisibility(View.VISIBLE);
            else {
                emptyView.setVisibility(View.GONE);
                mAdapter.submitList(restaurantItems);  //Display data on screen
            }
        });

        //SEARCH
        restaurantViewModel.mFilteredRestaurants.observe(this, restaurantItems -> {
            mAdapter.submitList(restaurantItems); //Display data on screen
        });

        mSearchEditText.setSearchTextChangedListener(charSequence -> {
            restaurantViewModel.searchInRestaurantList(mRestaurantItems, charSequence);
        });

        return rootView;
    }

    private void initRecyclerView(){

        // Improve performance if layout size fixed
        //mRecyclerView.setHasFixedSize(true);

        //Divider deco
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(Objects.requireNonNull(this.getContext()), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        mAdapter = new RestaurantAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnRestaurantClickListener(this);
    }

    public void setSearchEditText(){
        mSearchEditText.setVisibility(View.VISIBLE);
        mSearchEditText.setHint(R.string.search_hint_restaurant);
        mSearchEditText.requestFocus();
    }


    @Override
    public void onRestaurantClick(RestaurantItem restaurant) {

        startActivity(RestaurantDetailsActivity.newIntent(this.getContext(), restaurant.getPlaceId(), restaurant.getName()));
        Toast.makeText(getContext(), restaurant.getName(), Toast.LENGTH_SHORT).show();
    }
}
