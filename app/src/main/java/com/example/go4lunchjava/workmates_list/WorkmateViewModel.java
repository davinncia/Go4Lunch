package com.example.go4lunchjava.workmates_list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunchjava.repository.FireStoreRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WorkmateViewModel extends ViewModel {

    private FireStoreRepository mFireStoreRepository = FireStoreRepository.getInstance();

    private MutableLiveData<List<Workmate>> mUsersMutableLiveData = new MutableLiveData<>();
    LiveData<List<Workmate>> mUsersLiveData = mUsersMutableLiveData;

    public WorkmateViewModel() {

        getUsers();

    }

    private void getUsers(){

        mFireStoreRepository.getAllUserDocuments().addOnSuccessListener(queryDocumentSnapshots -> {

            List<Workmate> workmates = new ArrayList<>();

            for (QueryDocumentSnapshot document: queryDocumentSnapshots){

                workmates.add(new Workmate(String.valueOf(document.get(Workmate.FIELD_NAME)),
                        null,
                        String.valueOf(document.get(Workmate.FIELD_AVATAR)),
                        String.valueOf(document.get(Workmate.FIELD_RESTAURANT_ID)),
                        String.valueOf(document.get(Workmate.FIELD_RESTAURANT_NAME))));
            }

            if (workmates.size() > 0) mUsersMutableLiveData.setValue(workmates);
        });
    }
}
