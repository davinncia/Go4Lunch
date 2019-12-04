package com.example.go4lunchjava;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.workmates_list.WorkmateViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Iterator;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkmateViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    UsersFireStoreRepository mUsersFireStoreRepository;
    @Mock
    FirebaseAuth mFireBaseAuth;
    //@InjectMocks
    private WorkmateViewModel viewModel;
    @Mock
    QuerySnapshot mQuerySnapshot;


    @Before
    public void setUp(){

        viewModel = new WorkmateViewModel(mUsersFireStoreRepository, mFireBaseAuth);
    }

    @Test
    public void usersAreFetchAndMapCorrectly(){

        //when(mUsersFireStoreRepository.getAllUserDocuments()).thenReturn()
        when(mFireBaseAuth.getUid()).thenReturn("1234");
        when(mQuerySnapshot.iterator()).thenReturn(new Iterator<QueryDocumentSnapshot>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public QueryDocumentSnapshot next() {
                return null;
            }
        });

        Assert.assertNull(viewModel.mUsersLiveData.getValue());

    }
}
