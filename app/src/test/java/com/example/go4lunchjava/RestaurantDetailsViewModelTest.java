package com.example.go4lunchjava;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.go4lunchjava.repository.PlacesApiRepository;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.restaurant_details.RestaurantDetailsViewModel;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    @Mock
    RestaurantDetailsViewModel.GetRestaurantDetailsAsyncTask mDetailsAsyncTask;

    private RestaurantDetailsViewModel mDetailsViewModel;
    private String mPlaceID = "1";

    @Before
    public void setUp(){
        mDetailsViewModel = new RestaurantDetailsViewModel(mApplication, mPlacesRepo, mUsersRepo, mAuth);

    }

    @Test
    public void test(){
        //GIVEN

        //WHEN
        when(mDetailsAsyncTask.execute()).thenReturn(null);
        mDetailsViewModel.launchDetailsRequest(mPlaceID);

        //THEN
    }
}
