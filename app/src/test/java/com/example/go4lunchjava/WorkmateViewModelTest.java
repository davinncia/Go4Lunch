package com.example.go4lunchjava;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.example.go4lunchjava.workmates_list.Workmate;
import com.example.go4lunchjava.workmates_list.WorkmateViewModel;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkmateViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    UsersFireStoreRepository mUsersRepo;
    @Mock
    FirebaseAuth mFireBaseAuth;

    private WorkmateViewModel mViewModel;
    private List<Workmate> mWorkmates;

    private String currentUid = "1";
    private String id1 = "2";
    private String id2 = "3";


    @Test
    public void usersAreMappedCorrectly() throws InterruptedException {
        //GIVEN
        List<User> users = new ArrayList<>();
        users.add(new User(id1, "Phil", "", "1", "Burger", null));
        users.add(new User(id2, "Anne", "", "2", "Pizza", null));
        users.add(new User(currentUid, "Marie", "", "3", "Salad", null));

        //WHEN
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>(users));
        mViewModel = new WorkmateViewModel(mUsersRepo, mFireBaseAuth);

        mWorkmates = LiveDataTestUtil.getOrAwaitValue(mViewModel.mUsersLiveData);

        //THEN
        for (int i = 0; i < mWorkmates.size(); i++) {
            Assert.assertEquals(users.get(i).getId(), mWorkmates.get(i).getUid());
            Assert.assertEquals(users.get(i).getUser_name(), mWorkmates.get(i).getDisplayName());
            Assert.assertEquals(users.get(i).getRestaurant_id(), mWorkmates.get(i).getRestaurantId());
            Assert.assertEquals(users.get(i).getRestaurant_name(), mWorkmates.get(i).getRestaurantName());

        }
    }

    @Test
    public void currentUserIsNotInWorkmatesList() throws InterruptedException {
        //GIVEN
        List<User> users = new ArrayList<>();
        users.add(new User(id1, "Phil", "", "1", "Burger", null));
        users.add(new User(id2, "Anne", "", "2", "Pizza", null));
        users.add(new User(currentUid, "Marie", "", "3", "Salad", null));

        //WHEN
        when(mFireBaseAuth.getUid()).thenReturn(currentUid);
        when(mUsersRepo.getAllUserLiveData()).thenReturn(new MutableLiveData<>(users));
        mViewModel = new WorkmateViewModel(mUsersRepo, mFireBaseAuth);

        mWorkmates = LiveDataTestUtil.getOrAwaitValue(mViewModel.mUsersLiveData);

        //THEN
        for (Workmate workmate : mWorkmates){
            assertNotEquals(currentUid, workmate.getUid());
        }
        assertEquals(users.size() - 1, mWorkmates.size());

    }
}
