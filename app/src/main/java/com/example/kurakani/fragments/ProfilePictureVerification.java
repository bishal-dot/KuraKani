package com.example.kurakani.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.kurakani.R;
import com.example.kurakani.model.VerificationResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.HomePageActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private File photoFile;
    private ApiService apiService;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            if (photoFile != null && photoFile.exists()) {
                                capturedBitmap = decodeSampledBitmapFromFile(photoFile, 800, 800);
                                if (profileImageView != null && capturedBitmap != null) {
                                    profileImageView.setImageBitmap(capturedBitmap);
                                }
                            } else {
                                Toast.makeText(requireContext(), "Failed to capture image", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("ProfilePicVerif", "Error loading image from file", e);
                            Toast.makeText(requireContext(), "Camera processing failed", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Camera operation cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_picture_verification, container, false);

        profileImageView = view.findViewById(R.id.profileImage);
        btnCapture = view.findViewById(R.id.captureButton);
        btnVerify = view.findViewById(R.id.verifyButton);

        apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        btnCapture.setOnClickListener(v -> openCamera());
        btnVerify.setOnClickListener(v -> {
            if (capturedBitmap != null) {
                verifyPhoto();
            } else {
                Toast.makeText(requireContext(), "Capture a photo first", Toast.LENGTH_SHORT).show();
            }
        });

        // Restore photo file path
        if (savedInstanceState != null && savedInstanceState.containsKey("photoPath")) {
            String path = savedInstanceState.getString("photoPath");
            if (path != null) {
                photoFile = new File(path);
                if (photoFile.exists()) {
                    capturedBitmap = decodeSampledBitmapFromFile(photoFile, 800, 800);
                    profileImageView.setImageBitmap(capturedBitmap);
                }
            }
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photoFile != null) {
            outState.putString("photoPath", photoFile.getAbsolutePath());
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
        } else {
            launchCameraIntent();
        }
    }

    private void launchCameraIntent() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        requireContext().getPackageName() + ".provider",
                        photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                cameraLauncher.launch(intent);
            }
        } catch (Exception e) {
            Log.e("ProfilePicVerif", "Cannot open camera", e);
            Toast.makeText(requireContext(), "Cannot open camera: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalCacheDir();
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCameraIntent();
        } else {
            Toast.makeText(requireContext(), "Camera permission denied.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap decodeSampledBitmapFromFile(File file, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private void verifyPhoto() {
        btnVerify.setEnabled(false);
        btnVerify.setText("Verifying...");

        File file = bitmapToFile(capturedBitmap, "verification_photo.jpg");
        if (file == null) {
            Toast.makeText(requireContext(), "Failed to prepare image", Toast.LENGTH_SHORT).show();
            resetVerifyButton();
            return;
        }

        MultipartBody.Part photoPart = MultipartBody.Part.createFormData(
                "photo",
                file.getName(),
                RequestBody.create(MediaType.parse("image/jpeg"), file)
        );

        // Use login token here
        String token = requireContext().getSharedPreferences("KurakaniPrefs", Context.MODE_PRIVATE)
                .getString("auth_token", null);

        if (token == null) {
            Toast.makeText(requireContext(), "Login token missing. Please log in again.", Toast.LENGTH_SHORT).show();
            resetVerifyButton();
            return;
        }

        apiService.verifyGender("Bearer " + token, photoPart)
                .enqueue(new Callback<VerificationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<VerificationResponse> call,
                                           @NonNull Response<VerificationResponse> response) {
                        resetVerifyButton();
                        try {
                            if (response.isSuccessful() && response.body() != null) {
                                VerificationResponse res = response.body();
                                if (!res.isError()) {
                                    Toast.makeText(requireContext(), "Verification successful!", Toast.LENGTH_LONG).show();
                                    navigateToHome();
                                } else {
                                    String errorMsg = res.getMessage();
                                    if (res.getDetectedGender() != null && res.getUserGender() != null) {
                                        errorMsg += "\nDetected: " + res.getDetectedGender() +
                                                "\nExpected: " + res.getUserGender();
                                    }
                                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                                }
                            } else {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Toast.makeText(requireContext(), "Server error: " + errorBody, Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<VerificationResponse> call, @NonNull Throwable t) {
                        resetVerifyButton();
                        Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void resetVerifyButton() {
        if (btnVerify != null) {
            btnVerify.setEnabled(true);
            btnVerify.setText("Verify");
        }
    }

    private File bitmapToFile(Bitmap bitmap, String fileName) {
        try {
            File file = new File(requireContext().getCacheDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, fos);
            fos.flush();
            fos.close();
            return file;
        } catch (IOException e) {
            Log.e("ProfilePicVerif", "Error converting bitmap to file", e);
            return null;
        }
    }

    private void navigateToHome() {
        Intent intent = new Intent(getActivity(), HomePageActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) getActivity().finish();
    }
}
