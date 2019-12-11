package com.example.go4lunchjava;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.map.MapViewModel;
import com.example.go4lunchjava.map.Poi;
import com.example.go4lunchjava.places_api.pojo.Geometry;
import com.example.go4lunchjava.places_api.pojo.Location;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResponse;
import com.example.go4lunchjava.places_api.pojo.NearBySearchResult;
import com.example.go4lunchjava.repository.LocationRepository;
import com.example.go4lunchjava.repository.NetworkRepository;
import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MapViewModelUnitTest {

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
    NetworkRepository mNetworkRepo;

    private MapViewModel mViewModel;
    private List<Poi> mPoiList;

    @Before
    public void setUp() {

        when(mLocationRepo.getLatLngLiveData()).thenReturn(new MutableLiveData<>(new LatLng(12, 12)));
        when(mNetworkRepo.getNetworkStatusLiveData()).thenReturn(new MutableLiveData<>(true));
    }

    @Test
    public void listOfRestaurantsCorrectlyMappedGivenApiResponse() throws InterruptedException {
        //GIVEN
        Geometry geo = new Geometry(new Location(12.0, 16.0));

        List<NearBySearchResult> nearByRestaurants = new ArrayList<>();
        NearBySearchResult apiResult1 = new NearBySearchResult(geo, "name1", null, "1", 5f, "Rue resto");
        NearBySearchResult apiResult2 = new NearBySearchResult(geo, "name2", null, "2", 3f, "Avenue resto");
        NearBySearchResult apiResult3 = new NearBySearchResult(geo, "name3", null, "3", 1f, "Boulevard resto");

        nearByRestaurants.add(apiResult1);
        nearByRestaurants.add(apiResult2);
        nearByRestaurants.add(apiResult3);

        NearBySearchResponse apiResponse = new NearBySearchResponse(nearByRestaurants);

        when(mPlacesRepo.getNearByResponseLiveData()).thenReturn(new MutableLiveData<>(apiResponse));
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>());

        //WHEN
        mViewModel = new MapViewModel(mApplication, mLocationRepo, mPlacesRepo, mUsersRepo, mNetworkRepo);

        mPoiList = LiveDataTestUtil.getOrAwaitValue(mViewModel.mPoiListLiveData);

        //THEN
        assertEquals(3, mPoiList.size());
        assertEquals("1", mPoiList.get(0).getId());
        assertEquals("2", mPoiList.get(1).getId());
        assertEquals("3", mPoiList.get(2).getId());
        assertEquals(12.0, mPoiList.get(0).getLat(), 0.0);
        assertEquals(16.0, mPoiList.get(0).getLon(), 0.0);
    }

    @Test
    public void locationIsUpdated() throws InterruptedException {
        //GIVEN
        LatLng location;

        when(mLocationRepo.getLatLngLiveData()).thenReturn(new MutableLiveData<>(new LatLng(12, 12)));
        when(mPlacesRepo.getNearByResponseLiveData()).thenReturn(new MutableLiveData<>());
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>());

        //WHEN
        mViewModel = new MapViewModel(mApplication, mLocationRepo, mPlacesRepo, mUsersRepo, mNetworkRepo);
        mViewModel.hasLocationPermission(true);
        mViewModel.hasMapAvailability(true);
        mViewModel.setCameraMoved(false);

        location = LiveDataTestUtil.getOrAwaitValue(mViewModel.mLocationLiveData);

        //THEN
        assertEquals(new LatLng(12, 12), location);
    }


    @Test(expected = RuntimeException.class)
    public void locationNotUpdatedWhenMapIsMoving() throws InterruptedException {
        //GIVEN
        LatLng location;

        when(mPlacesRepo.getNearByResponseLiveData()).thenReturn(new MutableLiveData<>());
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>());

        //WHEN
        mViewModel = new MapViewModel(mApplication, mLocationRepo, mPlacesRepo, mUsersRepo, mNetworkRepo);
        mViewModel.hasLocationPermission(true);
        mViewModel.hasMapAvailability(true);
        mViewModel.setCameraMoved(true);

        location = LiveDataTestUtil.getOrAwaitValue(mViewModel.mLocationLiveData);

        //THEN
        //RunTimeException
    }

    @Test(expected = RuntimeException.class)
    public void locationNotUpdatedWhenMapNotAvailable() throws InterruptedException {
        //GIVEN
        LatLng location;

        when(mPlacesRepo.getNearByResponseLiveData()).thenReturn(new MutableLiveData<>());
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>());

        //WHEN
        mViewModel = new MapViewModel(mApplication, mLocationRepo, mPlacesRepo, mUsersRepo, mNetworkRepo);
        mViewModel.hasLocationPermission(true);
        mViewModel.hasMapAvailability(false);
        mViewModel.setCameraMoved(false);

        location = LiveDataTestUtil.getOrAwaitValue(mViewModel.mLocationLiveData);

        //THEN
        //RunTimeException
    }

    @Test(expected = RuntimeException.class)
    public void locationNotUpdatedWhenPermissionDenied() throws InterruptedException {
        //GIVEN
        LatLng location;

        when(mPlacesRepo.getNearByResponseLiveData()).thenReturn(new MutableLiveData<>());
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>());

        //WHEN
        mViewModel = new MapViewModel(mApplication, mLocationRepo, mPlacesRepo, mUsersRepo, mNetworkRepo);
        mViewModel.hasLocationPermission(false);
        mViewModel.hasMapAvailability(true);
        mViewModel.setCameraMoved(false);

        location = LiveDataTestUtil.getOrAwaitValue(mViewModel.mLocationLiveData);

        //THEN
        //RunTimeException
    }

    @Test
    public void listOfRestaurantsUpdatedWithWorkmateInfo() throws InterruptedException {
        //GIVEN
        Geometry geo = new Geometry(new Location(12.0, 16.0));

        List<NearBySearchResult> nearByRestaurants = new ArrayList<>();
        NearBySearchResult apiResult1 = new NearBySearchResult(geo, "name1", null, "1", 5f, "Rue resto");
        NearBySearchResult apiResult2 = new NearBySearchResult(geo, "name2", null, "2", 3f, "Avenue resto");

        nearByRestaurants.add(apiResult1);
        nearByRestaurants.add(apiResult2);

        NearBySearchResponse apiResponse = new NearBySearchResponse(nearByRestaurants);

        List<User> workmates = new ArrayList<>();
        workmates.add(new User(null, null, null, "2", null, null));

        when(mPlacesRepo.getNearByResponseLiveData()).thenReturn(new MutableLiveData<>(apiResponse));
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>(workmates));

        //WHEN
        mViewModel = new MapViewModel(mApplication, mLocationRepo, mPlacesRepo, mUsersRepo, mNetworkRepo);
        mPoiList = LiveDataTestUtil.awaitValue(mViewModel.mPoiListLiveData);

        //THEN
        assertEquals(R.drawable.ic_pointer_red, mPoiList.get(0).getPointerRes());
        assertEquals(R.drawable.ic_pointer_blue, mPoiList.get(1).getPointerRes());

    }

}
