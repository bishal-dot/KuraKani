package com.example.kurakani.views;

import android.app.Activity;
import android.content.Intent;
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
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etFullName, etAge, etPurpose, etJob, etEducation, etBio, etInterests;
    private ImageView profileImage, editImageButton;
    private MaterialButton saveButton, btnAddPhoto;
    private RecyclerView photosRecyclerView;

    private List<String> uploadedPhotos = new ArrayList<>();
    private PhotosAdapter photosAdapter;

    private ActivityResultLauncher<Intent> pickPhotoLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        initRecyclerView();
        initPhotoPicker();
        fetchUserProfile();

        editImageButton.setOnClickListener(v -> pickImage());
        btnAddPhoto.setOnClickListener(v -> pickImage());
        saveButton.setOnClickListener(v -> updateProfile());
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
    }

    private void initRecyclerView() {
        photosAdapter = new PhotosAdapter(this, uploadedPhotos);
        photosRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        photosRecyclerView.setAdapter(photosAdapter);
    }

    private void initPhotoPicker() {
        pickPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getData() != null) {
                            Uri photoUri = data.getData();
                            uploadedPhotos.add(photoUri.toString());
                            photosAdapter.notifyDataSetChanged();
                        }
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
                if (response.isSuccessful() && response.body() != null) {
                    ProfileResponse.User user = response.body().user;
                    if (user != null) {
                        etFullName.setText(user.fullname != null ? user.fullname : "");
                        etAge.setText(user.age > 0 ? String.valueOf(user.age) : "");
                        etPurpose.setText(user.purpose != null ? user.purpose : "");
                        etJob.setText(user.job != null ? user.job : "");
                        etEducation.setText(user.education != null ? user.education : "");
                        etBio.setText(user.bio != null ? user.bio : "");
                        etInterests.setText(user.interests != null ? user.interests.replace("[","").replace("]","").replace("\"","") : "");

                        Glide.with(EditProfileActivity.this)
                                .load(user.profile != null && !user.profile.isEmpty() ? user.profile : R.drawable.john)
                                .placeholder(R.drawable.john)
                                .error(R.drawable.john)
                                .into(profileImage);

                        // Preload user photos
                        uploadedPhotos.clear();
                        if (user.photos != null) {
                            for (ProfileResponse.User.Photo photo : user.photos) {
                                uploadedPhotos.add(photo.url);
                            }
                        }
                        photosAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error loading profile: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        saveButton.setEnabled(false);

        String fullname = etFullName.getText().toString().trim();

        int age = 0;
        try {
            String ageStr = etAge.getText().toString().trim();
            if (!ageStr.isEmpty()) age = Integer.parseInt(ageStr);
        } catch (NumberFormatException ignored) {}

        String purpose = etPurpose.getText().toString().trim();
        String job = etJob.getText().toString().trim();
        String education = etEducation.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        List<String> interestsList = new ArrayList<>();
        String interestsText = etInterests.getText().toString().trim();
        for (String s : interestsText.split(",")) {
            if (!s.trim().isEmpty()) interestsList.add(s.trim());
        }

        // Safe Base64 conversion
        String profileBase64 = "";
        if (profileImage.getDrawable() instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
            if (bitmap != null) profileBase64 = bitmapToBase64(bitmap);
        }

        ProfileRequest request = new ProfileRequest(
                fullname, age, "Male", // replace with dynamic gender if needed
                profileBase64, purpose, job,
                interestsList, education, bio
        );

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.completeProfile("Bearer " + getAuthToken(), request).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                saveButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated!", Toast.LENGTH_SHORT).show();

                    // Pass updated data back to ProfileScreen
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("updatedFullName", fullname);
                    setResult(Activity.RESULT_OK, resultIntent);

                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Update failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                saveButton.setEnabled(true);
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return android.util.Base64.encodeToString(byteArrayOutputStream.toByteArray(), android.util.Base64.DEFAULT);
    }

    private String getAuthToken() {
        return getSharedPreferences("KurakaniPrefs", MODE_PRIVATE).getString("auth_token", "");
    }
}
