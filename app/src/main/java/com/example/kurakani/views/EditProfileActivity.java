package com.example.kurakani.views;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kurakani.R;
import com.example.kurakani.Adapter.PhotosAdapter;
import com.example.kurakani.model.ProfileRequest;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.model.UploadPhotosResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements PhotosAdapter.PhotoClickListener {

    private TextInputEditText etFullName, etAge, etPurpose, etJob, etEducation, etBio, etInterests;
    private ImageView profileImage, editImageButton;
    private MaterialButton saveButton, btnAddPhoto;
    private RecyclerView photosRecyclerView;

    private List<ProfileResponse.UserPhoto> uploadedPhotos = new ArrayList<>();
    private PhotosAdapter photosAdapter;
    private ActivityResultLauncher<Intent> pickPhotoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Check token first
        SharedPreferences prefs = getSharedPreferences("KurakaniPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initRecyclerView();
        initPhotoPicker();
        fetchUserProfile(); // fetch profile from backend

        // Back button
        ImageView backButton = findViewById(R.id.backButton);
        if (backButton != null) backButton.setOnClickListener(v -> finish());
    }

    private void initViews() {
        profileImage = findViewById(R.id.profileImage);
        editImageButton = findViewById(R.id.editImageButton);
        etFullName = findViewById(R.id.etFullName);
        etAge = findViewById(R.id.etAge);
        etPurpose = findViewById(R.id.etPurpose);
        etJob = findViewById(R.id.etJob);
        etEducation = findViewById(R.id.etEducation);
        etBio = findViewById(R.id.etBio);
        etInterests = findViewById(R.id.etInterests);
        photosRecyclerView = findViewById(R.id.photosRecyclerView);
        btnAddPhoto = findViewById(R.id.btnAddPhoto);
        saveButton = findViewById(R.id.saveButton);

        editImageButton.setOnClickListener(v -> pickImage());
        btnAddPhoto.setOnClickListener(v -> pickImage());
        saveButton.setOnClickListener(v -> updateProfile());
    }

    private void initRecyclerView() {
        photosAdapter = new PhotosAdapter(this, uploadedPhotos, this);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photosAdapter);
    }

    private void initPhotoPicker() {
        pickPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri photoUri = result.getData().getData();
                        if (photoUri != null) uploadPhotoToServer(photoUri);
                    }
                });
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhotoLauncher.launch(intent);
    }

    private void fetchUserProfile() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);

        apiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().user != null) {
                    ProfileResponse.User user = response.body().user;
                    etFullName.setText(user.fullname != null ? user.fullname : "");
                    etAge.setText(user.age != null ? String.valueOf(user.age) : "");
                    etPurpose.setText(user.purpose != null ? user.purpose : "");
                    etJob.setText(user.job != null ? user.job : "");
                    etEducation.setText(user.education != null ? user.education : "");
                    etBio.setText(user.about != null ? user.about : "");
                    etInterests.setText(user.interests != null ? String.join(", ", user.interests) : "");

                    Glide.with(EditProfileActivity.this)
                            .load(user.profile != null && !user.profile.isEmpty() ? user.profile : R.drawable.john)
                            .placeholder(R.drawable.john)
                            .into(profileImage);

                    uploadedPhotos.clear();
                    if (user.photos != null) uploadedPhotos.addAll(user.getUserPhotos());
                    photosAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error fetching profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadPhotoToServer(Uri photoUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] imageBytes = baos.toByteArray();

            RequestBody requestFile = RequestBody.create(imageBytes, MediaType.parse("image/jpeg"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("photos[]", "photo.jpg", requestFile);

            ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
            apiService.uploadPhotos(List.of(body)).enqueue(new Callback<UploadPhotosResponse>() {
                @Override
                public void onResponse(Call<UploadPhotosResponse> call, Response<UploadPhotosResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        for (UploadPhotosResponse.Photo p : response.body().photos) {
                            uploadedPhotos.add(new ProfileResponse.UserPhoto(p.id, p.url));
                        }
                        photosAdapter.notifyDataSetChanged();
                        Toast.makeText(EditProfileActivity.this, "Photo uploaded successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadPhotosResponse> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "Photo upload failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateProfile() {
        String fullname = etFullName.getText().toString().trim();
        int age = 0;
        try { age = Integer.parseInt(etAge.getText().toString().trim()); } catch (Exception ignored) {}
        String purpose = etPurpose.getText().toString().trim();
        String job = etJob.getText().toString().trim();
        String education = etEducation.getText().toString().trim();
        String about = etBio.getText().toString().trim();

        List<String> interestsList = new ArrayList<>();
        for (String s : etInterests.getText().toString().trim().split(",")) {
            if (!s.trim().isEmpty()) interestsList.add(s.trim());
        }

        String profileBase64 = "";
        if (profileImage.getDrawable() instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            profileBase64 = android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT);
        }

        ProfileRequest request = new ProfileRequest(
                fullname, age, "Male", profileBase64, purpose, job, interestsList, education, about
        );

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.updateProfile(request).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error updating profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPhotoDeleteClick(int position, ProfileResponse.UserPhoto photo) {
        // implement photo delete if needed
    }
}
