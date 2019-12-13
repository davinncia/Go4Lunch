package com.example.go4lunchjava.workmates_list;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.go4lunchjava.R;
import com.example.go4lunchjava.chat.ChatActivity;
import com.example.go4lunchjava.di.ViewModelFactory;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class WorkmatesFragment extends Fragment implements WorkmateAdapter.WorkmateClickListener {

    private RecyclerView mRecyclerView;
    private WorkmateAdapter mAdapter;

    public WorkmatesFragment() {
        // Required empty public constructor
    }

    public static WorkmatesFragment newInstance(){
        return new WorkmatesFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workmates, container, false);

        mRecyclerView = rootView.findViewById(R.id.recycler_view_workmates);

        initRecyclerView();

        //ViewModel
        ViewModelFactory factory = ViewModelFactory.getInstance(requireActivity().getApplication());
        WorkmateViewModel viewModel = ViewModelProviders.of(this, factory).get(WorkmateViewModel.class);
        viewModel.mUsersLiveData.observe(this, users -> mAdapter.updateData(users));

        // Inflate the layout for this fragment
        return rootView;
    }

    private void initRecyclerView(){

        mAdapter = new WorkmateAdapter(this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true); //Performance
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public void onWorkmateClick(String workmateId) {

        String currentUid = FirebaseAuth.getInstance().getUid();
        if (workmateId == null || currentUid == null) return;

        startActivity(ChatActivity.navigate(getContext(), currentUid, workmateId));
    }
}
