package com.example.kurakani;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kurakani.model.Match;

import java.util.Arrays;
import java.util.List;

public class ProfileMatches extends Fragment {

    private RecyclerView recyclerView;
    private MatchAdapter adapter;

    public ProfileMatches() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_matches, container, false);
        recyclerView = view.findViewById(R.id.recyclerMatches);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Match> fakeMatches = Arrays.asList(
                new Match("John" ,"Loves hiking", R.drawable.john, 28)
        );

        adapter = new MatchAdapter(getContext(), fakeMatches);
        recyclerView.setAdapter(adapter);

        return view;
    }
}