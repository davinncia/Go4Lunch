package com.example.go4lunchjava;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResponse;
import com.example.go4lunchjava.places_api.pojo.details.RestaurantDetailsResult;
import com.example.go4lunchjava.places_api.pojo.details.hours.Close;
import com.example.go4lunchjava.places_api.pojo.details.hours.Open;
import com.example.go4lunchjava.places_api.pojo.details.hours.OpeningHoursDetails;
import com.example.go4lunchjava.places_api.pojo.details.hours.OpeningPeriod;
import com.example.go4lunchjava.repository.LocationRepository;
import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.restaurant_list.RestaurantItem;
import com.example.go4lunchjava.restaurant_list.RestaurantListViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantListViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    Application mApplication;
    @Mock
    PlacesApiRepository mPlacesRepo;
    @Mock
    UsersFireStoreRepository mUsersRepo;
    @Mock
    LocationRepository mLocationRepo;
    @Mock
    FirebaseAuth mAuth;

    private RestaurantListViewModel mViewModel;
    private List<RestaurantItem> mRestaurants;
    private String mCurrentUid = "66";
    private String mRestoId = "Resto";

    @Before
    public void setUp() {

        when(mLocationRepo.getLatLngLiveData()).thenReturn(new MutableLiveData<>(new LatLng(12, 12)));
        when(mAuth.getUid()).thenReturn(mCurrentUid);
    }

    @Test
    public void listOfRestaurantsCorrectlyMappedGivenApiResponse() throws InterruptedException {
        //GIVEN
        List<NearBySearchResult> nearByRestaurants = new ArrayList<>();
        NearBySearchResult apiResult1 = new NearBySearchResult(null, "name1", null, "1", 5f, "Rue resto");
        NearBySearchResult apiResult2 = new NearBySearchResult(null, "name2", null, "2", 3f, "Avenue resto");
        NearBySearchResult apiResult3 = new NearBySearchResult(null, "name3", null, "3", 1f, "Boulevard resto");

        nearByRestaurants.add(apiResult1);
        nearByRestaurants.add(apiResult2);
        nearByRestaurants.add(apiResult3);

        NearBySearchResponse apiResponse = new NearBySearchResponse(nearByRestaurants);

        when(mPlacesRepo.getNearByResponseLiveData()).thenReturn(new MutableLiveData<>(apiResponse));
        when(mPlacesRepo.getHoursDetailLiveData()).thenReturn(new MutableLiveData<>());
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>());

        //WHEN
        mViewModel = new RestaurantListViewModel(mApplication, mUsersRepo, mPlacesRepo, mAuth, mLocationRepo);
        mViewModel.init(RestaurantListViewModel.NEARBY_SEARCH);

        mRestaurants = LiveDataTestUtil.getOrAwaitValue(mViewModel.mRestaurantsLiveData);

        //THEN
        assertEquals(3, mRestaurants.size());

        //RESTO 1
        assertEquals("name1", mRestaurants.get(0).getName());
        assertEquals("1", mRestaurants.get(0).getPlaceId());
        assertEquals("Rue resto", mRestaurants.get(0).getAddress());
        assertEquals(R.drawable.ic_star_three, mRestaurants.get(0).getRatingResource());

        //RESTO 2
        assertEquals("name2", mRestaurants.get(1).getName());
        assertEquals("2", mRestaurants.get(1).getPlaceId());
        assertEquals("Avenue resto", mRestaurants.get(1).getAddress());
        assertEquals(R.drawable.ic_star_two, mRestaurants.get(1).getRatingResource());

        //RESTO 3
        assertEquals("name3", mRestaurants.get(2).getName());
        assertEquals("3", mRestaurants.get(2).getPlaceId());
        assertEquals("Boulevard resto", mRestaurants.get(2).getAddress());
        assertEquals(R.drawable.ic_star, mRestaurants.get(2).getRatingResource());
    }


    @Test
    public void specificRestaurantCorrectlyMappedGivenApiResponse() throws InterruptedException {
        //GIVEN
        RestaurantDetailsResult apiResult = new RestaurantDetailsResult(
                "www.website.com", "name", "1", 5f, "Rue resto", "0606060606");
        RestaurantDetailsResponse apiResponse = new RestaurantDetailsResponse(apiResult);

        when(mPlacesRepo.getDetailsResponseLiveData()).thenReturn(new MutableLiveData<>(apiResponse));

        //WHEN
        mViewModel = new RestaurantListViewModel(mApplication, mUsersRepo, mPlacesRepo, mAuth, mLocationRepo);
        mViewModel.init("1");

        mRestaurants = LiveDataTestUtil.getOrAwaitValue(mViewModel.mRestaurantsLiveData);

        //THEN
        assertEquals("name", mRestaurants.get(0).getName());
        assertEquals("Rue resto", mRestaurants.get(0).getAddress());
        assertEquals(R.drawable.ic_star_three, mRestaurants.get(0).getRatingResource());
        assertEquals("1", mRestaurants.get(0).getPlaceId());
    }

    @Test
    public void numberOfWorkmatesUpdatedGivenFireStoreResponse() throws InterruptedException {
        //GIVEN
        List<User> users = new ArrayList<>();
        users.add(new User("workmate", "Anne", "", mRestoId, null, null));

        List<NearBySearchResult> nearByRestaurants = new ArrayList<>();
        NearBySearchResult apiResult1 = new NearBySearchResult(null, "name1", null, "1", 5f, null);
        NearBySearchResult apiResult2 = new NearBySearchResult(null, "name2", null, mRestoId, 3f, null);

        nearByRestaurants.add(apiResult1);
        nearByRestaurants.add(apiResult2);

        NearBySearchResponse apiResponse = new NearBySearchResponse(nearByRestaurants);

        when(mPlacesRepo.getNearByResponseLiveData()).thenReturn(new MutableLiveData<>(apiResponse));
        when(mPlacesRepo.getHoursDetailLiveData()).thenReturn(new MutableLiveData<>());
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>(users));

        //WHEN
        mViewModel = new RestaurantListViewModel(mApplication, mUsersRepo, mPlacesRepo, mAuth, mLocationRepo);
        mViewModel.init(RestaurantListViewModel.NEARBY_SEARCH);

        mRestaurants = LiveDataTestUtil.awaitValue(mViewModel.mRestaurantsLiveData);

        //THEN
        assertEquals(0, mRestaurants.get(0).getWorkmatesNbr());
        assertEquals(1, mRestaurants.get(1).getWorkmatesNbr());

    }

    @Test
    public void hourDetailsUpdatedGivenApiResponse() throws InterruptedException {
        //GIVEN
        int day = 2; //Monday

        OpeningHoursDetails hourDetails = new OpeningHoursDetails(new OpeningPeriod[]
                {new OpeningPeriod(new Open(day, "0000"), new Close(day, "0000"))});

        List<NearBySearchResult> nearByRestaurants = new ArrayList<>();
        NearBySearchResult apiResult1 = new NearBySearchResult(null, "name1", null, "1", 5f, "Rue resto");

        nearByRestaurants.add(apiResult1);

        NearBySearchResponse apiResponse = new NearBySearchResponse(nearByRestaurants);

        when(mPlacesRepo.getHoursDetailLiveData()).thenReturn(new MutableLiveData<>(Collections.singletonList(hourDetails)));
        when(mPlacesRepo.getNearByResponseLiveData()).thenReturn(new MutableLiveData<>(apiResponse));
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>());

        //WHEN
        mViewModel = new RestaurantListViewModel(mApplication, mUsersRepo, mPlacesRepo, mAuth, mLocationRepo);
        mViewModel.init(RestaurantListViewModel.NEARBY_SEARCH);

        mRestaurants = LiveDataTestUtil.awaitValue(mViewModel.mRestaurantsLiveData);

        //THEN
        assertEquals("Closed today", mRestaurants.get(0).getHours());

    }


}