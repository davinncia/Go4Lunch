package com.example.go4lunchjava;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.go4lunchjava.map.MapViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class MapViewModelUnitTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Application mApplication;
    private MapViewModel viewModel;

    @Before
    public void setUp(){

        mApplication = Mockito.mock(Application.class);
        viewModel = new MapViewModel(mApplication);

    }
}
