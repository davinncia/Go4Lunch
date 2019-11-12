package com.example.go4lunchjava.workmates_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.auth.User;
import com.example.go4lunchjava.repository.FireStoreRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkmateViewModel extends ViewModel {

    private FireStoreRepository mFireStoreRepository = FireStoreRepository.getInstance();

    private MutableLiveData<List<User>> mUsersMutableLiveData = new MutableLiveData<>();
    public LiveData<List<User>> mUsersLiveData = mUsersMutableLiveData;

    public WorkmateViewModel() {

        getUsers();

    }

    private void getUsers(){

        mFireStoreRepository.getAllUserDocuments().addOnSuccessListener(queryDocumentSnapshots -> {

            List<User> users = new ArrayList<>();

            for (QueryDocumentSnapshot document: queryDocumentSnapshots){

                users.add(new User(String.valueOf(document.get(User.FIELD_NAME)),
                        null,
                        String.valueOf(document.get(User.FIELD_AVATAR)),
                        null));
            }

            if (users.size() > 0) mUsersMutableLiveData.setValue(users);
        });
    }
}
