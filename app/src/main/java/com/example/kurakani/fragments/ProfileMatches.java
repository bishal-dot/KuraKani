package com.example.kurakani.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kurakani.Adapter.MatchAdapter;
import com.example.kurakani.R;
import com.example.kurakani.viewmodel.MatchModel;
import com.example.kurakani.viewmodel.MatchViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileMatches extends Fragment {

    private RecyclerView recyclerView;
    private MatchAdapter adapter;
    private List<MatchModel> matchList = new ArrayList<>();

    public ProfileMatches() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerMatches);
        adapter = new MatchAdapter(getContext(), matchList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        MatchViewModel matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);
        matchViewModel.getMatchList().observe(getViewLifecycleOwner(), updatedMatches -> {
            matchList.clear();
            matchList.addAll(updatedMatches);
            adapter.notifyDataSetChanged();
        });
    }
}
