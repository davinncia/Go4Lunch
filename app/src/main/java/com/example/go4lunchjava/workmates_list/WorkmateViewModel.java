package com.example.go4lunchjava.workmates_list;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.repository.UsersFireStoreRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkmateViewModel extends ViewModel {

    private UsersFireStoreRepository mFireStoreRepository;
    private FirebaseAuth mAuth;

    private MutableLiveData<List<Workmate>> mUsersMutableLiveData = new MutableLiveData<>();
    public LiveData<List<Workmate>> mUsersLiveData = mUsersMutableLiveData;

    public WorkmateViewModel(UsersFireStoreRepository usersFireStoreRepository, FirebaseAuth auth) {

        this.mFireStoreRepository = usersFireStoreRepository;
        this.mAuth = auth;
        getUsers();

    }

    private void getUsers(){

        mFireStoreRepository.getAllUserDocuments().addOnSuccessListener(queryDocumentSnapshots -> {

            mapWorkmates(queryDocumentSnapshots);
        });
    }

    @VisibleForTesting
    void mapWorkmates(QuerySnapshot queryDocumentSnapshots) {
        List<Workmate> workmates = new ArrayList<>();

        //TODO mock iterable
        for (QueryDocumentSnapshot document: queryDocumentSnapshots){

            if (!document.getId().equals(mAuth.getUid())) {

                workmates.add(new Workmate(String.valueOf(document.get(Workmate.FIELD_NAME)),
                        document.getId(),
                        String.valueOf(document.get(Workmate.FIELD_AVATAR)),
                        String.valueOf(document.get(Workmate.FIELD_RESTAURANT_ID)),
                        String.valueOf(document.get(Workmate.FIELD_RESTAURANT_NAME))));
            }
        }

        if (workmates.size() > 0) mUsersMutableLiveData.setValue(workmates);
    }
}
