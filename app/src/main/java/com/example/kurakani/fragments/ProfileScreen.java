package com.example.kurakani.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kurakani.R;
import com.example.kurakani.Adapter.PhotosAdapter;
import com.example.kurakani.model.DeletePhotoResponse;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.EditProfileActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;

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
    private MaterialCardView uploadPhotoBanner;
    private TextView tvUploadBanner;
    private List<ProfileResponse.User.UserPhoto> userPhotos = new ArrayList<>();
    private static final int EDIT_PROFILE_REQUEST = 1001;

    public ProfileScreen() {}

    @Override
    public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_screen, container, false);

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
        uploadPhotoBanner = view.findViewById(R.id.uploadPhotoBanner);
        tvUploadBanner = view.findViewById(R.id.tvUploadBanner);

        photosAdapter = new PhotosAdapter(requireContext(), userPhotos, this);
        photoStripRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        photoStripRecycler.setAdapter(photosAdapter);

        backButton.setOnClickListener(v -> { if (isAdded()) getActivity().onBackPressed(); });

        tvUploadBanner.setOnClickListener(v -> openEditProfile());
        btnEditProfile.setOnClickListener(v -> openEditProfile());

        fetchProfile();
        return view;
    }

    private void openEditProfile() {
        if (!isAdded() || getActivity() == null) return;
        startActivityForResult(new Intent(requireActivity(), EditProfileActivity.class), EDIT_PROFILE_REQUEST);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchProfile();
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

                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    ProfileResponse.User user = response.body().getUser();

                    tvUsername.setText(user.username != null ? user.username : user.fullname);
                    tvFullName.setText(user.fullname != null ? user.fullname : tvUsername.getText().toString());
                    tvAge.setText(user.age != null ? String.valueOf(user.age) : "-");
                    tvGender.setText(user.gender != null ? user.gender : "-");
                    tvPurpose.setText(user.purpose != null ? user.purpose : "-");
                    tvJob.setText(user.job != null ? user.job : "-");
                    tvEducation.setText(user.education != null ? user.education : "-");
                    tvBio.setText(user.about != null ? user.about : "-");
                    tvInterests.setText(user.interests != null && !user.interests.isEmpty() ? TextUtils.join(", ", user.interests) : "-");
                    tvMatches.setText(String.valueOf(user.matches_count));

                    String profileUrl = user.profile != null && !user.profile.trim().isEmpty() ? user.profile.trim() : null;
                    Glide.with(requireContext())
                            .load(profileUrl != null ? profileUrl + "?t=" + System.currentTimeMillis() : R.drawable.default_avatar)
                            .placeholder(R.drawable.default_avatar)
                            .error(R.drawable.default_avatar)
                            .circleCrop()
                            .into(ivProfilePhoto);

                    uploadPhotoBanner.setVisibility(profileUrl == null ? View.VISIBLE : View.GONE);

                    userPhotos.clear();
                    if (user.photos != null && !user.photos.isEmpty()) {
                        for (ProfileResponse.User.UserPhoto p : user.photos) {
                            userPhotos.add(new ProfileResponse.User.UserPhoto(p.id, p.url));
                        }
                    }
                    photosAdapter.notifyDataSetChanged();

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            String newProfileUrl = data.getStringExtra("profile_url");
            if (newProfileUrl != null && !newProfileUrl.isEmpty()) {
                Glide.with(requireContext())
                        .load(newProfileUrl + "?t=" + System.currentTimeMillis())
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .circleCrop()
                        .into(ivProfilePhoto);
                uploadPhotoBanner.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onPhotoDeleteClick(int position, ProfileResponse.User.UserPhoto photo) {
        if (!isAdded()) return;

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
    }
}
