package com.example.go4lunchjava;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResponse;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResult;
import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.restaurant_details.RestaurantDetails;
import com.example.go4lunchjava.restaurant_details.RestaurantDetailsViewModel;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantDetailsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    Application mApplication;
    @Mock
    PlacesApiRepository mPlacesRepo;
    @Mock
    UsersFireStoreRepository mUsersRepo;
    @Mock
    FirebaseAuth mAuth;

    private RestaurantDetailsViewModel mDetailsViewModel;
    private List<Workmate> uiWorkmates;
    private String mCurrentUid = "66";
    private String mPlaceID = "1";

    @Before
    public void setUp(){

        when(mAuth.getUid()).thenReturn(mCurrentUid);
    }

    @Test
    public void restaurantDetailsIsCorrectlyMappedGivenApiResponse() throws InterruptedException {
        //GIVEN
        List<User> users = new ArrayList<>();
        users.add(new User(mCurrentUid, "Anne", "", mPlaceID, "Burger", Arrays.asList("Burger", "Pizza")));

        RestaurantDetailsResult apiResult = new RestaurantDetailsResult(
                "www.website.com", "name", mPlaceID, 5f, "Rue resto", "0606060606");
        RestaurantDetailsResponse apiResponse = new RestaurantDetailsResponse(apiResult);

        //WHEN
        when(mPlacesRepo.getDetailsResponseLiveData()).thenReturn(new MutableLiveData<>(apiResponse));
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>(users));
        mDetailsViewModel = new RestaurantDetailsViewModel(mApplication, mPlacesRepo, mUsersRepo, mAuth);

        mDetailsViewModel.launchDetailsRequest(mPlaceID);
        mDetailsViewModel.fetchFireStoreData();

        uiWorkmates = LiveDataTestUtil.getOrAwaitValue(mDetailsViewModel.mWorkmatesLiveData);
        RestaurantDetails uiDetails = LiveDataTestUtil.getOrAwaitValue(mDetailsViewModel.mDetailsLiveData);

        //THEN
        Assert.assertNotNull(uiDetails);
        Assert.assertEquals("name", uiDetails.getName());
        Assert.assertEquals(R.drawable.ic_star_three, uiDetails.getRatingResource());
        Assert.assertEquals("Rue resto", uiDetails.getAddress());
        Assert.assertEquals("www.website.com", uiDetails.getWebSiteUrl());
        Assert.assertEquals("0606060606", uiDetails.getPhoneNumber());

    }

    @Test
    public void workmatesAreSelectedOnlyIfGoingToRestaurant() throws InterruptedException {
        //GIVEN
        List<User> users = new ArrayList<>();
        users.add(new User(mCurrentUid, "Anne", "", mPlaceID, "Burger", Arrays.asList("Burger", "Pizza")));
        users.add(new User("3", "Mike", "", mPlaceID, "Pizza", Arrays.asList("Salad", "Pizza")));
        users.add(new User("4", "Camille", "", "77", "Pizza", Arrays.asList("Salad", "Pizza")));

        //WHEN
        when(mPlacesRepo.getDetailsResponseLiveData()).thenReturn(new MutableLiveData<>());
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>(users));
        mDetailsViewModel = new RestaurantDetailsViewModel(mApplication, mPlacesRepo, mUsersRepo, mAuth);

        mDetailsViewModel.launchDetailsRequest(mPlaceID);
        mDetailsViewModel.fetchFireStoreData();

        uiWorkmates = LiveDataTestUtil.getOrAwaitValue(mDetailsViewModel.mWorkmatesLiveData);

        //THEN
        Assert.assertEquals(1, uiWorkmates.size());
        Assert.assertEquals(users.get(1).getUser_name(), uiWorkmates.get(0).getDisplayName());
    }

    @Test
    public void userSelectionIsCorrectlyUpdated() throws InterruptedException {
        //GIVEN
        boolean isSelected;

        List<User> users = new ArrayList<>();
        users.add(new User(mCurrentUid, "Anne", "", mPlaceID, "Burger", Arrays.asList("Burger", "Pizza")));

        //WHEN
        when(mPlacesRepo.getDetailsResponseLiveData()).thenReturn(new MutableLiveData<>());
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>(users));
        mDetailsViewModel = new RestaurantDetailsViewModel(mApplication, mPlacesRepo, mUsersRepo, mAuth);

        mDetailsViewModel.launchDetailsRequest(mPlaceID);
        mDetailsViewModel.fetchFireStoreData();

        uiWorkmates = LiveDataTestUtil.getOrAwaitValue(mDetailsViewModel.mWorkmatesLiveData);
        isSelected = LiveDataTestUtil.getOrAwaitValue(mDetailsViewModel.mIsUserSelectionLiveData);

        //THEN
        Assert.assertTrue(isSelected);
    }

    @Test
    public void userFavoritesAreCorrectlyUpdated() throws InterruptedException {
        //GIVEN
        boolean isFavorite;

        List<User> users = new ArrayList<>();
        users.add(new User(mCurrentUid, "Anne", "", mPlaceID, "Burger", Arrays.asList("Burger", mPlaceID)));

        //WHEN
        when(mPlacesRepo.getDetailsResponseLiveData()).thenReturn(new MutableLiveData<>());
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>(users));
        mDetailsViewModel = new RestaurantDetailsViewModel(mApplication, mPlacesRepo, mUsersRepo, mAuth);

        mDetailsViewModel.launchDetailsRequest(mPlaceID);
        mDetailsViewModel.fetchFireStoreData();

        uiWorkmates = LiveDataTestUtil.getOrAwaitValue(mDetailsViewModel.mWorkmatesLiveData);
        isFavorite = LiveDataTestUtil.getOrAwaitValue(mDetailsViewModel.mIsUserFavLiveData);

        //THEN
        Assert.assertTrue(isFavorite);
    }
}
