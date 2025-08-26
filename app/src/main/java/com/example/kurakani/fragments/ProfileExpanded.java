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
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.viewmodel.ProfileModel;
import com.example.kurakani.model.ProfileResponse;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileExpanded extends Fragment {

    private static final String ARG_PROFILE = "arg_profile";
    private static final String ARG_USER_ID = "arg_user_id";

    private MaterialToolbar topAppBar;
    private ProfileModel profile;
    private int userId = -1;

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

    public static ProfileExpanded newInstance(int userId) {
        ProfileExpanded fragment = new ProfileExpanded();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            profile = (ProfileModel) getArguments().getSerializable(ARG_PROFILE);
            userId = getArguments().getInt(ARG_USER_ID, -1);
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

        if (profile == null && userId != -1) {
            fetchProfileFromApi(userId);
        } else if (profile != null) {
            displayProfile(profile);
        }

        return view;
    }

    private void fetchProfileFromApi(int userId) {
        ApiService api = RetrofitClient.getInstance(requireContext()).create(ApiService.class);
        api.getUserProfile(userId).enqueue(new Callback<ProfileResponse.User>() {
            @Override
            public void onResponse(Call<ProfileResponse.User> call, Response<ProfileResponse.User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    profile = convertApiUserToProfile(response.body());
                    displayProfile(profile);
                } else {
                    Log.e("ProfileExpanded", "API response empty for userId: " + userId);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse.User> call, Throwable t) {
                Log.e("ProfileExpanded", "Failed to load profile: " + t.getMessage());
            }
        });
    }

    private ProfileModel convertApiUserToProfile(ProfileResponse.User apiUser) {
        int age = apiUser.age != null ? apiUser.age : 0;
        String fullname = apiUser.fullname != null ? apiUser.fullname : "";
        String username = apiUser.username != null ? apiUser.username : "";
        String gender = apiUser.gender != null ? apiUser.gender : "";
        String purpose = apiUser.purpose != null ? apiUser.purpose : "";
        String about = apiUser.about != null ? apiUser.about : "";

        String profileUrl = "";
        if (apiUser.profile != null && !apiUser.profile.isEmpty()) {
            profileUrl = apiUser.profile.startsWith("http") ? apiUser.profile : RetrofitClient.BASE_URL + "storage/" + apiUser.profile;
        }

        List<String> interests = apiUser.interests != null ? apiUser.interests : new ArrayList<>();
        List<String> photos = new ArrayList<>();
        if (apiUser.photos != null) {
            for (ProfileResponse.User.Photo photo : apiUser.photos) {
                if (photo != null && photo.url != null && !photo.url.isEmpty()) {
                    String fullUrl = photo.url.startsWith("http") ? photo.url : RetrofitClient.BASE_URL + "storage/" + photo.url;
                    photos.add(fullUrl);
                }
            }
        }

        return new ProfileModel(apiUser.id, fullname, username, age, gender, purpose, about, profileUrl, interests, photos);
    }

    private void displayProfile(ProfileModel profile) {
        Log.d("ProfileExpanded", "Loaded profile: " + profile.getFullname());

        tvName.setText(profile.getFullname() + ", " + profile.getAge());
        tvPurpose.setText(!profile.getPurpose().isEmpty() ? profile.getPurpose() : "No purpose");
        tvBio.setText(!profile.getAbout().isEmpty() ? profile.getAbout() : "No bio available");

        String profileUrl = profile.getProfile();
        if (profileUrl != null && !profileUrl.isEmpty()) {
            Glide.with(requireContext())
                    .load(profileUrl)
                    .placeholder(R.drawable.john)
                    .into(imgProfile);
        }

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

        List<String> photos = profile.getPhotos();
        if (photos != null && !photos.isEmpty()) {
            for (int i = 0; i < photos.size(); i++) {
                String url = photos.get(i);
                if (!url.startsWith("http")) {
                    photos.set(i, RetrofitClient.BASE_URL + "storage/" + url);
                }
            }
            OtherProfilePhotosAdapter adapter = new OtherProfilePhotosAdapter(requireContext(), photos);
            recyclerPhotos.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
            recyclerPhotos.setAdapter(adapter);
        }
    }
}
