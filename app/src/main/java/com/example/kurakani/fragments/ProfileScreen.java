package com.example.kurakani.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kurakani.R;
import com.example.kurakani.Adapter.PhotosAdapter;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.EditProfileActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileScreen extends Fragment {

    private TextView tvUsername, tvFullName, tvAge, tvGender, tvPurpose, tvJob, tvEducation, tvBio, tvInterests, tvMatches;
    private ImageView ivProfilePhoto, backButton;
    private MaterialButton btnEditProfile;

    private RecyclerView photoStripRecycler;
    private PhotosAdapter photosAdapter;
    private List<String> userPhotos = new ArrayList<>();

    public ProfileScreen() { }

    @Override
    public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
                                          Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(R.layout.fragment_profile_screen, container, false);

        // Bind views
        tvUsername = view.findViewById(R.id.tvUsername);
        tvFullName = view.findViewById(R.id.tvFullName);
        tvAge = view.findViewById(R.id.tvAge);
        tvGender = view.findViewById(R.id.tvGender);
        tvPurpose = view.findViewById(R.id.tvPurpose);
        tvJob = view.findViewById(R.id.tvJob);
        tvEducation = view.findViewById(R.id.tvEducation);
        tvBio = view.findViewById(R.id.tvBio);
        tvInterests = view.findViewById(R.id.tvInterests);
        tvMatches = view.findViewById(R.id.tvMatches);
        ivProfilePhoto = view.findViewById(R.id.ivProfilePhoto);
        backButton = view.findViewById(R.id.backButton);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        photoStripRecycler = view.findViewById(R.id.photoStrip);

        backButton.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        btnEditProfile.setOnClickListener(v -> {
            if (getActivity() != null) {
                startActivity(new Intent(getActivity(), EditProfileActivity.class));
            }
        });

        // RecyclerView setup
        photosAdapter = new PhotosAdapter(getContextSafe(), userPhotos);
        photoStripRecycler.setLayoutManager(new LinearLayoutManager(getContextSafe(), LinearLayoutManager.HORIZONTAL, false));
        photoStripRecycler.setAdapter(photosAdapter);

        fetchProfile();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchProfile(); // Refresh profile after returning from EditProfileActivity
    }

    private void fetchProfile() {
        Context context = getContextSafe();
        if (context == null) return;

        SharedPreferences prefs = context.getSharedPreferences("KurakaniPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", "");

        if (token.isEmpty()) {
            Toast.makeText(context, "Token not found, try login/signup again", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient(context).create(ApiService.class);
        apiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse.User user = response.body().user;
                    if (user != null) {
                        tvUsername.setText(user.username != null && !user.username.isEmpty() ? user.username : user.fullname);
                        tvFullName.setText(user.fullname != null && !user.fullname.isEmpty() ? user.fullname : tvUsername.getText().toString());
                        tvAge.setText(user.age > 0 ? String.valueOf(user.age) : "-");
                        tvGender.setText(user.gender != null ? user.gender : "-");
                        tvPurpose.setText(user.purpose != null ? user.purpose : "-");
                        tvJob.setText(user.job != null ? user.job : "-");
                        tvEducation.setText(user.education != null ? user.education : "-");
                        tvBio.setText(user.bio != null ? user.bio : "-");
                        tvInterests.setText(user.interests != null ? user.interests.replace("[","").replace("]","").replace("\"","") : "-");
                        tvMatches.setText(String.valueOf(user.matches_count));

                        Glide.with(context)
                                .load(user.profile != null && !user.profile.isEmpty() ? user.profile : R.drawable.john)
                                .placeholder(R.drawable.john)
                                .error(R.drawable.john)
                                .into(ivProfilePhoto);

                        // Update photos
                        userPhotos.clear();
                        if (user.photos != null) {
                            for (ProfileResponse.User.Photo photo : user.photos) {
                                userPhotos.add(photo.url);
                            }
                        }
                        photosAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Profile not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Context context = getContextSafe();
                if (context != null)
                    Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Context getContextSafe() {
        return isAdded() ? getContext() : null;
    }
}
