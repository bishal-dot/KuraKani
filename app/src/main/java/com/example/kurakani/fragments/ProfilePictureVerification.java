package com.example.kurakani.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kurakani.R;
import com.example.kurakani.model.VerificationResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
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
    private String userGender; // "male" or "female"
    private String authToken;
    private ApiService apiService;

    private ActivityResultLauncher<Intent> cameraLauncher;

    private static final int CAMERA_REQUEST_CODE = 1001;

    @Nullable
    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          @Nullable android.view.ViewGroup container,
                                          @Nullable Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(R.layout.fragment_profile_verification, container, false);

        profileImageView = view.findViewById(R.id.profileImage);
        btnCapture = view.findViewById(R.id.captureButton);
        btnVerify = view.findViewById(R.id.verifyButton);

        userGender = getArguments() != null ? getArguments().getString("user_gender", "male") : "male";

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        authToken = "Bearer " + requireActivity()
                .getSharedPreferences("KurakaniPrefs", Activity.MODE_PRIVATE)
                .getString("auth_token", "");

        // --- Camera Activity Result ---
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            capturedBitmap = (Bitmap) extras.get("data");
                            if (capturedBitmap != null) {
                                profileImageView.setImageBitmap(capturedBitmap);
                                Toast.makeText(getContext(), "Photo captured! Now click Verify.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
        );

        btnCapture.setOnClickListener(v -> openCamera());
        btnVerify.setOnClickListener(v -> {
            if (capturedBitmap == null) {
                Toast.makeText(getContext(), "Please capture a photo first", Toast.LENGTH_SHORT).show();
            } else {
                uploadPhotoAndVerify(capturedBitmap, userGender);
            }
        });

        return view;
    }

    private void openCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            cameraLauncher.launch(intent);
        } else {
            Toast.makeText(getContext(), "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadPhotoAndVerify(Bitmap bitmap, String gender) {
        File file = bitmapToFile(bitmap, "live_photo.jpg");
        if (file == null) {
            Toast.makeText(getContext(), "Failed to process image", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/jpeg"));
        MultipartBody.Part body = MultipartBody.Part.createFormData("photo", file.getName(), requestFile);
        RequestBody userGenderBody = RequestBody.create(gender, MediaType.parse("text/plain"));

        Call<VerificationResponse> call = apiService.verifyGender(authToken, body, userGenderBody);
        call.enqueue(new Callback<VerificationResponse>() {
            @Override
            public void onResponse(@NonNull Call<VerificationResponse> call, @NonNull Response<VerificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VerificationResponse res = response.body();
                    if (!res.isError()) {
                        Toast.makeText(getContext(), "Verification successful!", Toast.LENGTH_SHORT).show();
                        navigateNextFragment();
                    } else {
                        Toast.makeText(getContext(), "Verification failed: " + res.getMessage(), Toast.LENGTH_LONG).show();
                        goBackToLogin();
                    }
                } else {
                    Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VerificationResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private File bitmapToFile(Bitmap bitmap, String fileName) {
        File file = new File(requireContext().getCacheDir(), fileName);
        try {
            file.createNewFile();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] bitmapData = bos.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void navigateNextFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profileContainer, new com.example.kurakani.fragments.HomePageFragment())
                .commit();
    }

    private void goBackToLogin() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
