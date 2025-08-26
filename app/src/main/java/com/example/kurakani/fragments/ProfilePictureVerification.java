package com.example.kurakani.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kurakani.R;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.model.VerificationResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.HomePageActivity;
import com.example.kurakani.views.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePictureVerification extends Fragment {

    private ImageView profileImageView;
    private Button btnCapture, btnVerify;
    private Bitmap capturedBitmap;
    private String userGender, tempToken, profilePhotoBase64;
    private ApiService apiService;

    private static final int MAX_ATTEMPTS = 5;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_picture_verification, container, false);

        profileImageView = view.findViewById(R.id.profileImage);
        btnCapture = view.findViewById(R.id.captureButton);
        btnVerify = view.findViewById(R.id.verifyButton);

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        if (getArguments() != null) {
            userGender = getArguments().getString("user_gender");
            tempToken = getArguments().getString("temp_token");
            profilePhotoBase64 = getArguments().getString("profile_photo_base64");
        }

        ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        capturedBitmap = (Bitmap) result.getData().getExtras().get("data");
                        profileImageView.setImageBitmap(capturedBitmap);
                    }
                });

        btnCapture.setOnClickListener(v -> openCamera(cameraLauncher));
        btnVerify.setOnClickListener(v -> {
            if (capturedBitmap != null) verifyPhoto();
        });

        return view;
    }

    private void openCamera(ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        launcher.launch(intent);
    }

    private void verifyPhoto() {
        File file = bitmapToFile(capturedBitmap, "live_photo.jpg");
        if (file == null) return;

        MultipartBody.Part photoPart = MultipartBody.Part.createFormData(
                "photo", file.getName(), RequestBody.create(file, MediaType.parse("image/jpeg"))
        );

        RequestBody genderBody = RequestBody.create(userGender, MediaType.parse("text/plain"));
        RequestBody tempTokenBody = RequestBody.create(tempToken, MediaType.parse("text/plain"));

        apiService.verifyGenderTemp(photoPart, genderBody, tempTokenBody)
                .enqueue(new Callback<VerificationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VerificationResponse> call, @NonNull Response<VerificationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            VerificationResponse res = response.body();
                            if (!res.isError()) finalizeProfile();
                            else handleFailedVerification(res.getAttempts(), res.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<VerificationResponse> call, @NonNull Throwable t) {}
                });
    }

    private void finalizeProfile() {
        apiService.finalizeProfile(tempToken).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().error) {
                    navigateToHome();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {}
        });
    }

    private void handleFailedVerification(int attempts, String message) {
        if (attempts >= MAX_ATTEMPTS) {
            Toast.makeText(getContext(), "Maximum attempts reached. Account deleted.", Toast.LENGTH_LONG).show();
            goBackToLogin();
        } else {
            Toast.makeText(getContext(), message + " Attempt " + attempts + " of " + MAX_ATTEMPTS, Toast.LENGTH_LONG).show();
        }
    }

    private File bitmapToFile(Bitmap bitmap, String fileName) {
        File file = new File(requireContext().getCacheDir(), fileName);
        try {
            file.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) { e.printStackTrace(); return null; }
    }

    private void navigateToHome() {
        Intent intent = new Intent(getActivity(), HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void goBackToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
