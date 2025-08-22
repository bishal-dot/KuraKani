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
import com.example.kurakani.model.DeletePhotoResponse;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.AuthActivity;
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

        // RecyclerView setup safely using requireContext()
        photosAdapter = new PhotosAdapter(requireContext(), userPhotos, this);
        photoStripRecycler.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        photoStripRecycler.setAdapter(photosAdapter);

        // Back button
        backButton.setOnClickListener(v -> {
            if (isAdded()) getActivity().onBackPressed();
        });

        // Edit Profile button
        btnEditProfile.setOnClickListener(v -> {
            if (!isAdded() || getActivity() == null) {
                Toast.makeText(getContext(), "Cannot edit profile now", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Intent intent = new Intent(requireActivity(), EditProfileActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to open Edit Profile", Toast.LENGTH_SHORT).show();
            }
        });
//        btnEditProfile.setOnClickListener(v -> {
//            if (!isAdded() || getActivity() == null) return;
//            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
//            startActivity(intent);
//        });

        fetchProfile();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchProfile(); // Refresh profile after returning from EditProfileActivity
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
                        // Username / Full name
                        tvUsername.setText(user.username != null && !user.username.isEmpty() ? user.username : user.fullname);
                        tvFullName.setText(user.fullname != null && !user.fullname.isEmpty() ? user.fullname : tvUsername.getText().toString());

                        // Age and gender
                        tvAge.setText(user.age != null ? String.valueOf(user.age) : "-");
                        tvGender.setText(user.gender != null ? user.gender : "-");

                        // Purpose, Job, Education
                        tvPurpose.setText(user.purpose != null ? user.purpose : "-");
                        tvJob.setText(user.job != null ? user.job : "-");
                        tvEducation.setText(user.education != null ? user.education : "-");

                        // Bio / About
                        tvBio.setText(user.bio != null ? user.bio : user.about != null ? user.about : "-");

                        // Interests (list to comma-separated string)
                        if (user.interests != null && !user.interests.isEmpty()) {
                            tvInterests.setText(String.join(", ", user.interests));
                        } else {
                            tvInterests.setText("-");
                        }

                        // Matches count
                        tvMatches.setText(String.valueOf(user.matches_count));

                        // Profile image
                        Glide.with(requireContext())
                                .load(user.profile != null ? user.profile : R.drawable.john)
                                .placeholder(R.drawable.john)
                                .error(R.drawable.john)
                                .into(ivProfilePhoto);

                        // Photos strip
                        userPhotos.clear();
                        if (user.photos != null && !user.photos.isEmpty()) {
                            userPhotos.addAll(user.getUserPhotos());
                        }
                        photosAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(requireContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    String msg = "Failed to load profile";
                    if (response.body() != null && response.body().message != null) msg += ": " + response.body().message;
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
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
                                // Fade-out animation
                                RecyclerView.ViewHolder viewHolder = photoStripRecycler.findViewHolderForAdapterPosition(position);
                                if (viewHolder != null) {
                                    viewHolder.itemView.animate()
                                            .alpha(0f)
                                            .setDuration(300)
                                            .withEndAction(() -> {
                                                userPhotos.remove(position);
                                                photosAdapter.notifyItemRemoved(position);
                                            })
                                            .start();
                                } else {
                                    userPhotos.remove(position);
                                    photosAdapter.notifyItemRemoved(position);
                                }

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
