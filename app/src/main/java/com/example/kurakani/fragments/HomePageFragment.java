package com.example.kurakani.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.Adapter.ProfileAdapter;
import com.example.kurakani.Adapter.StoryAdapter;
import com.example.kurakani.R;
import com.example.kurakani.model.StoryModel;
import com.example.kurakani.model.ProfileModel;
import com.example.kurakani.model.MatchModel;
import com.example.kurakani.viewmodel.MatchViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomePageFragment extends Fragment {

    private RecyclerView recyclerViewStory, recyclerViewProfiles;

    private final List<ProfileModel> matchedList = new ArrayList<>();

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MatchViewModel matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);

        recyclerViewStory = view.findViewById(R.id.recyclerViewStory);
        recyclerViewProfiles = view.findViewById(R.id.recyclerViewProfiles);

        // === Setup for Stories ===
        List<StoryModel> storyList = new ArrayList<>();
        storyList.add(new StoryModel(R.drawable.john, "Bishal"));
        storyList.add(new StoryModel(R.drawable.kori, "Bishwash"));
        storyList.add(new StoryModel(R.drawable.john, "Binay"));
        // Add more as needed

        recyclerViewStory.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewStory.setAdapter(new StoryAdapter(getContext(), storyList, story -> {
            // Handle story click
            // Example: open profile or toast
            // Toast.makeText(getContext(), "Story: " + story.getName(), Toast.LENGTH_SHORT).show();
        }));

        // === Setup for Profiles ===
        List<ProfileModel> profileList = new ArrayList<>();
        profileList.add(new ProfileModel(R.drawable.kori, "Hancy Bishwash", 23, "Exploring code, cosmos","Acoustic Music"));
        profileList.add(new ProfileModel(R.drawable.john, "Bishal", 23, "Exploring code, cosmos","Acoustic Music"));
        profileList.add(new ProfileModel(R.drawable.john, "Binay Bunu", 23, "Exploring code, cosmos","Acoustic Music"));
        // Add more as needed

        recyclerViewProfiles.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerViewProfiles.setAdapter(new ProfileAdapter(getContext(), profileList, matchedUser -> {
            MatchModel match = new MatchModel(
                matchedUser.getName(),
                    matchedUser.getBio(),
                    matchedUser.getImageResId(),
                    matchedUser.getAge()
            );
            matchViewModel.addMatch(match);
        }));
    }
}