package com.example.go4lunchjava;

import android.app.Application;
import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.go4lunchjava.map.MapViewModel;
import com.example.go4lunchjava.places_api.PlacesApiService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class MapViewModelUnitTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Application mApplication;
    private MapViewModel viewModel;

    @Mock
    private PlacesApiService mPlacesApiService;

    @Before
    public void setUp(){

        //MockitoAnnotations.initMocks(this);
        //TODO NINO: can we mock application like that ?
        mApplication = Mockito.mock(Application.class);
        viewModel = new MapViewModel(mApplication);

    }

    @Test
    public void locationLiveDataUpdatesOnlyIfMapIsReadyAndHasPermission(){
        //GIVEN

        //Mock places service
        Mockito.when(mPlacesApiService.nearbySearch("location", 0)).thenReturn(null);

        //WHEN
        viewModel.hasLocationPermission(true);
        viewModel.hasMapAvailability(true);

        //THEN
        Assert.assertNull(viewModel.mPoiListLiveData);
    }
}
