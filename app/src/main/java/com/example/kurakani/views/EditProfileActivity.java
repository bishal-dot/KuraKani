package com.example.kurakani.views;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.example.kurakani.model.DeletePhotoResponse;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.model.UploadPhotosResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity implements PhotosAdapter.PhotoClickListener {

    private TextInputEditText etFullName, etAge, etPurpose, etJob, etEducation, etBio, etInterests;
    private ImageView profileImage, editImageButton;
    private MaterialCardView btnAddPhoto;
    private MaterialButton saveButton;
    private RecyclerView photosRecyclerView;

    private List<ProfileResponse.User.UserPhoto> uploadedPhotos = new ArrayList<>();
    private PhotosAdapter photosAdapter;
    private ActivityResultLauncher<Intent> pickPhotoLauncher;
    private boolean isPickingProfilePhoto = false;
    private List<Uri> selectedPhotoUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        initRecyclerView();
        initPhotoPicker();
        fetchUserProfile();

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
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

        editImageButton.setOnClickListener(v -> pickImage(true));
        btnAddPhoto.setOnClickListener(v -> pickImage(false));
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
                        if (isPickingProfilePhoto) {
                            Uri photoUri = result.getData().getData();
                            if (photoUri != null) uploadProfilePhoto(photoUri);
                        } else {
                            selectedPhotoUris.clear();
                            if (result.getData().getClipData() != null) {
                                int count = result.getData().getClipData().getItemCount();
                                for (int i = 0; i < count; i++) {
                                    selectedPhotoUris.add(result.getData().getClipData().getItemAt(i).getUri());
                                }
                            } else if (result.getData().getData() != null) {
                                selectedPhotoUris.add(result.getData().getData());
                            }
                            if (!selectedPhotoUris.isEmpty()) uploadMultiplePhotos(selectedPhotoUris);
                        }
                    }
                });
    }


    private void pickImage(boolean isProfile) {
        isPickingProfilePhoto = isProfile;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (!isProfile) intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickPhotoLauncher.launch(intent);
    }
    private void fetchUserProfile() {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    ProfileResponse.User user = response.body().getUser();

                    etFullName.setText(user.fullname != null ? user.fullname : "");
                    etAge.setText(user.age != null ? String.valueOf(user.age) : "");
                    etPurpose.setText(user.purpose != null ? user.purpose : "");
                    etJob.setText(user.job != null ? user.job : "");
                    etEducation.setText(user.education != null ? user.education : "");
                    etBio.setText(user.about != null ? user.about : "");
                    etInterests.setText(user.interests != null && !user.interests.isEmpty() ? String.join(", ", user.interests) : "");

                    String profileUrl = (user.profile != null && !user.profile.trim().isEmpty()) ? user.profile.trim() : null;
                    Glide.with(EditProfileActivity.this)
                            .load(profileUrl != null ? profileUrl : R.drawable.john)
                            .placeholder(R.drawable.john)
                            .error(R.drawable.john)
                            .circleCrop()
                            .into(profileImage);

                    uploadedPhotos.clear();
                    if (user.photos != null) {
                        for (ProfileResponse.User.UserPhoto p : user.photos) {
                            uploadedPhotos.add(new ProfileResponse.User.UserPhoto(p.id, p.url));
                        }
                    }
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

    private void uploadProfilePhoto(Uri photoUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(photoUri);
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();

            RequestBody requestFile = RequestBody.create(bytes, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("profile", "profile.jpg", requestFile);

            ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
            apiService.uploadProfilePhoto(body).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String updatedProfileUrl = response.body().get("profile_url").getAsString();

                            Glide.with(EditProfileActivity.this)
                                    .load(updatedProfileUrl + "?t=" + System.currentTimeMillis())
                                    .circleCrop()
                                    .placeholder(R.drawable.john)
                                    .error(R.drawable.john)
                                    .into(profileImage);

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("profile_url", updatedProfileUrl);
                            setResult(Activity.RESULT_OK, resultIntent);

                            Toast.makeText(EditProfileActivity.this, "Profile photo updated!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(EditProfileActivity.this, "Failed to get uploaded photo URL", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Failed to upload profile photo", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadMultiplePhotos(List<Uri> uris) {
        List<MultipartBody.Part> parts = new ArrayList<>();
        try {
            for (int i = 0; i < uris.size(); i++) {
                InputStream inputStream = getContentResolver().openInputStream(uris.get(i));
                byte[] bytes = inputStream.readAllBytes();
                inputStream.close();
                RequestBody requestFile = RequestBody.create(bytes, MediaType.parse("image/*"));
                parts.add(MultipartBody.Part.createFormData("photos[]", "photo_" + i + ".jpg", requestFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to read images", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.uploadPhotos(parts).enqueue(new Callback<UploadPhotosResponse>() {
            @Override
            public void onResponse(Call<UploadPhotosResponse> call, Response<UploadPhotosResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().error) {
                    for (UploadPhotosResponse.Photo p : response.body().photos) {
                        uploadedPhotos.add(new ProfileResponse.User.UserPhoto(p.id, p.url));
                    }
                    photosAdapter.notifyDataSetChanged();
                    selectedPhotoUris.clear();
                    Toast.makeText(EditProfileActivity.this, "Photos uploaded successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to upload photos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadPhotosResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error uploading photos: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String fullname = etFullName.getText().toString().trim();
        String ageText = etAge.getText().toString().trim();
        int age = 0;
        try { age = Integer.parseInt(ageText); } catch (Exception ignored) {}
        String purpose = etPurpose.getText().toString().trim();
        String job = etJob.getText().toString().trim();
        String education = etEducation.getText().toString().trim();
        String about = etBio.getText().toString().trim();

        List<String> interestsList = new ArrayList<>();
        for (String s : etInterests.getText().toString().trim().split(",")) {
            if (!s.trim().isEmpty()) interestsList.add(s.trim());
        }

        Map<String, Object> map = new HashMap<>();
        if (!fullname.isEmpty()) map.put("fullname", fullname);
        if (age > 0) map.put("age", age);
        if (!purpose.isEmpty()) map.put("purpose", purpose);
        if (!job.isEmpty()) map.put("job", job);
        if (!education.isEmpty()) map.put("education", education);
        if (!about.isEmpty()) map.put("about", about);
        if (!interestsList.isEmpty()) map.put("interests", interestsList);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.updateProfile(map).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPhotoDeleteClick(int position, ProfileResponse.User.UserPhoto photo) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        apiService.deletePhoto(photo.id).enqueue(new Callback<DeletePhotoResponse>() {
            @Override
            public void onResponse(Call<DeletePhotoResponse> call, Response<DeletePhotoResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().error) {
                    uploadedPhotos.remove(position);
                    photosAdapter.notifyItemRemoved(position);
                    Toast.makeText(EditProfileActivity.this, "Photo deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Failed to delete photo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DeletePhotoResponse> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Error deleting photo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
