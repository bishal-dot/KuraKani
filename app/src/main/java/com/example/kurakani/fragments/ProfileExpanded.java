package com.example.kurakani.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kurakani.Adapter.OtherProfilePhotosAdapter;
import com.example.kurakani.R;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.viewmodel.ProfileModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class ProfileExpanded extends Fragment {

    private static final String ARG_PROFILE = "arg_profile";

    private MaterialToolbar topAppBar;
    private ProfileModel profile;

    private ImageView imgProfile;
    private TextView tvName, tvPurpose, tvBio;
    private ChipGroup chipGroup;
    private RecyclerView recyclerPhotos;

    public static ProfileExpanded newInstance(ProfileModel profile) {
        ProfileExpanded fragment = new ProfileExpanded();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PROFILE, profile);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            profile = (ProfileModel) getArguments().getSerializable(ARG_PROFILE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_expanded, container, false);

        topAppBar = view.findViewById(R.id.topAppBar);
        imgProfile = view.findViewById(R.id.imgMainProfile);
        tvName = view.findViewById(R.id.tvNameAge);
        tvPurpose = view.findViewById(R.id.tvPurpose);
        tvBio = view.findViewById(R.id.tvBio);
        chipGroup = view.findViewById(R.id.chipGroupInterests);
        recyclerPhotos = view.findViewById(R.id.recyclerOtherPhotos);

        topAppBar.setNavigationOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        if (profile != null) {
            // Log for debugging
            Log.d("ProfileExpanded", "Loaded profile: " + profile.getFullname());

            // Name + Age
            tvName.setText(profile.getFullname() + ", " + profile.getAge());

            // Purpose & Bio
            tvPurpose.setText(!profile.getPurpose().isEmpty() ? profile.getPurpose() : "No purpose");
            tvBio.setText(!profile.getAbout().isEmpty() ? profile.getAbout() : "No bio available");

            // Profile Image
            String profileUrl = profile.getProfile();
            if (profileUrl != null && !profileUrl.isEmpty()) {
                if (!profileUrl.startsWith("http")) {
                    profileUrl = RetrofitClient.BASE_URL + "storage/" + profileUrl;
                }
                Glide.with(requireContext())
                        .load(profileUrl)
                        .placeholder(R.drawable.john)
                        .into(imgProfile);
            }

            // Interests as Chips
            List<String> interests = profile.getInterests();
            chipGroup.removeAllViews();
            if (interests != null && !interests.isEmpty()) {
                for (String interest : interests) {
                    Chip chip = new Chip(requireContext());
                    chip.setText(interest);
                    chip.setClickable(false);
                    chip.setChipBackgroundColorResource(R.color.chip_bg);
                    chipGroup.addView(chip);
                }
            } else {
                Chip chip = new Chip(requireContext());
                chip.setText("No interests");
                chip.setClickable(false);
                chip.setChipBackgroundColorResource(R.color.chip_bg);
                chipGroup.addView(chip);
            }

            // Other Photos
            List<String> photos = profile.getPhotos();
            if (photos != null && !photos.isEmpty()) {
                for (int i = 0; i < photos.size(); i++) {
                    String url = photos.get(i);
                    if (!url.startsWith("http")) {
                        photos.set(i, RetrofitClient.BASE_URL + "storage/" + url);
                    }
                }
                OtherProfilePhotosAdapter adapter = new OtherProfilePhotosAdapter(requireContext(), photos);
                recyclerPhotos.setLayoutManager(
                        new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                );
                recyclerPhotos.setAdapter(adapter);
            }
        }

        return view;
    }
}
