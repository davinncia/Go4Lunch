package com.example.go4lunchjava.workmates_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class WorkmateViewModel extends ViewModel {

    private UsersFireStoreRepository mFireStoreRepo;
    private FirebaseAuth mAuth;

    public LiveData<List<Workmate>> mUsersLiveData;

    public WorkmateViewModel(UsersFireStoreRepository usersFireStoreRepository, FirebaseAuth auth) {

        this.mFireStoreRepo = usersFireStoreRepository;
        this.mAuth = auth;
        //getUsers();

        mFireStoreRepo.fetchAllUsersDocuments();
        mUsersLiveData = Transformations.map(mFireStoreRepo.getAllUserLiveData(), users -> {

            List<Workmate> workmates = new ArrayList<>();

            for (User user : users){

                if (!user.getId().equals(mAuth.getUid())) {
                    workmates.add(new Workmate(user.getUser_name(),
                            user.getId(),
                            user.getAvatar_uri(),
                            user.getRestaurant_id(),
                            user.getRestaurant_name()));
                }
            }

            return workmates;
        });
    }

    //TODO: Search algorithm


}
