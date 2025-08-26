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
import com.example.kurakani.model.DeletePhotoResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.EditProfileActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileScreen extends Fragment implements PhotosAdapter.PhotoClickListener {

    private TextView tvUsername, tvFullName, tvAge, tvGender, tvPurpose, tvJob, tvEducation, tvBio, tvInterests, tvMatches;
    private ImageView ivProfilePhoto, backButton;
    private MaterialButton btnEditProfile;
    private RecyclerView photoStripRecycler;
    private PhotosAdapter photosAdapter;
    private List<ProfileResponse.UserPhoto> userPhotos = new ArrayList<>();

    private static final int EDIT_PROFILE_REQUEST = 1001;

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

        // RecyclerView
        photosAdapter = new PhotosAdapter(requireContext(), userPhotos, this);
        photoStripRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        photoStripRecycler.setAdapter(photosAdapter);

        backButton.setOnClickListener(v -> {
            if (isAdded()) getActivity().onBackPressed();
        });

        btnEditProfile.setOnClickListener(v -> {
            if (!isAdded() || getActivity() == null) return;
            Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
            startActivityForResult(intent, EDIT_PROFILE_REQUEST);
        });

        fetchProfile();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchProfile(); // ensure refresh after edit
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == android.app.Activity.RESULT_OK) {
            fetchProfile(); // Refresh after profile update
        }
    }

    private void fetchProfile() {
        if (!isAdded()) return;

        SharedPreferences prefs = requireContext().getSharedPreferences("KurakaniPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", "");

        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "Token not found, try login/signup again", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
        apiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && !response.body().error) {
                    ProfileResponse.User user = response.body().user;

                    if (user != null) {
                        tvUsername.setText(user.username != null ? user.username : user.fullname);
                        tvFullName.setText(user.fullname != null ? user.fullname : tvUsername.getText().toString());
                        tvAge.setText(user.age != null ? String.valueOf(user.age) : "-");
                        tvGender.setText(user.gender != null ? user.gender : "-");
                        tvPurpose.setText(user.purpose != null ? user.purpose : "-");
                        tvJob.setText(user.job != null ? user.job : "-");
                        tvEducation.setText(user.education != null ? user.education : "-");
                        tvBio.setText(user.about != null ? user.about : "-");
                        tvInterests.setText(user.interests != null ? String.join(", ", user.interests) : "-");
                        tvMatches.setText(String.valueOf(user.matches_count));

                        String profileUrl = user.profile != null ? user.profile.trim() : null;

                        // Glide with cache bypass
                        Glide.with(requireContext())
                                .load(profileUrl != null ? profileUrl + "?t=" + System.currentTimeMillis() : R.drawable.profile_icon)
                                .placeholder(R.drawable.default_avatar)
                                .error(R.drawable.default_avatar)
                                .circleCrop()
                                .into(ivProfilePhoto);

                        // Photos
                        userPhotos.clear();
                        if (user.photos != null && !user.photos.isEmpty()) {
                            userPhotos.addAll(user.getUserPhotos());
                        }
                        photosAdapter.notifyDataSetChanged();
                    }

                } else {
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                if (isAdded())
                    Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPhotoDeleteClick(int position, ProfileResponse.UserPhoto photo) {
        if (!isAdded()) return;
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Photo")
                .setMessage("Are you sure you want to delete this photo?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    ApiService apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);
                    apiService.deletePhoto(photo.id).enqueue(new Callback<DeletePhotoResponse>() {
                        @Override
                        public void onResponse(Call<DeletePhotoResponse> call, Response<DeletePhotoResponse> response) {
                            if (!isAdded()) return;

                            if (response.isSuccessful() && response.body() != null && !response.body().error) {
                                userPhotos.remove(position);
                                photosAdapter.notifyItemRemoved(position);
                                Toast.makeText(requireContext(), "Photo deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Failed to delete photo", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<DeletePhotoResponse> call, Throwable t) {
                            if (isAdded())
                                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
