package com.example.kurakani.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kurakani.R;
import com.example.kurakani.model.ProfileRequest;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.LoginActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSetupFragment extends Fragment {

    private static final int PICK_PROFILE_PHOTO = 101;

    private TextInputEditText etFullname, etAge, etPurpose, etInterests, etBio, etJob, etEducation;
    private RadioGroup rgGender;
    private Button btnSaveProfile;
    private ImageView profilePhoto;
    private Bitmap profileBitmap;

    public ProfileSetupFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_setup_card, container, false);

        etFullname = view.findViewById(R.id.etFullname);
        etAge = view.findViewById(R.id.etAge);
        rgGender = view.findViewById(R.id.genderOption);
        etPurpose = view.findViewById(R.id.etPurpose);
        etInterests = view.findViewById(R.id.etInterests);
        etBio = view.findViewById(R.id.etBio);
        etJob = view.findViewById(R.id.etJob);
        etEducation = view.findViewById(R.id.etEducation);

        profilePhoto = view.findViewById(R.id.profilePhoto);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        profilePhoto.setOnClickListener(v -> openGallery(PICK_PROFILE_PHOTO));
        btnSaveProfile.setOnClickListener(v -> {
            if (validateInputs()) encodeAndSendProfile();
        });

        return view;
    }

    private void openGallery(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), data.getData());
                bitmap = resizeBitmap(bitmap, 800, 800);
                if (requestCode == PICK_PROFILE_PHOTO) {
                    profilePhoto.setImageBitmap(bitmap);
                    profileBitmap = bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to select image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap == null) return null;
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(getFullname())) { etFullname.setError("Full name required"); return false; }
        if (getAge() < 18) { etAge.setError("You must be 18+"); return false; }
        String gender = getGender();
        if (gender == null || (!gender.equals("male") && !gender.equals("female"))) {
            Toast.makeText(getContext(), "Select valid gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public String getFullname() { return etFullname.getText().toString().trim(); }
    public int getAge() {
        try { return Integer.parseInt(etAge.getText().toString().trim()); }
        catch(Exception e){ return 0; }
    }
    public String getGender() {
        int id = rgGender.getCheckedRadioButtonId();
        if (id != -1) return ((RadioButton) requireView().findViewById(id)).getText().toString().toLowerCase();
        return null;
    }
    public String getPurpose() { return etPurpose.getText().toString().trim(); }
    public String getInterests() { return etInterests.getText().toString().trim(); }
    public String getBio() { return etBio.getText().toString().trim(); }
    public String getJob() { return etJob.getText().toString().trim(); }
    public String getEducation() { return etEducation.getText().toString().trim(); }
    public Bitmap getProfilePhoto() { return profileBitmap; }

    private void encodeAndSendProfile() {
        new Thread(() -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            profileBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            String encodedPhoto = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

            requireActivity().runOnUiThread(() -> sendProfileToServer(encodedPhoto));
        }).start();
    }

    private void sendProfileToServer(String photoBase64) {
        List<String> interestsList = new ArrayList<>();
        String raw = getInterests();
        if (!TextUtils.isEmpty(raw)) {
            for (String s : raw.split(",")) {
                String v = s.trim();
                if (!v.isEmpty()) interestsList.add(v);
            }
        }

        ProfileRequest request = new ProfileRequest(
                getFullname(),
                getAge(),
                getGender(),
                photoBase64,
                getPurpose(),
                getJob(),
                interestsList,
                getEducation(),
                getBio()
        );

        Context ctx = getContext() != null ? getContext() : getActivity();
        if (ctx == null) { Toast.makeText(getContext(), "Context not available", Toast.LENGTH_SHORT).show(); return; }

        Activity activity = getActivity();
        if (activity == null) return;
        String token = activity.getSharedPreferences("KurakaniPrefs", Activity.MODE_PRIVATE)
                .getString("auth_token", null);
        if (token == null) {
            startActivity(new Intent(activity, LoginActivity.class));
            activity.finish();
            return;
        }

        ApiService apiService = RetrofitClient.getClient(ctx).create(ApiService.class);

        apiService.completeProfileTemp("Bearer " + token, request)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String json = response.body().string();
                                JSONObject obj = new JSONObject(json);

                                boolean error = obj.optBoolean("error");
                                String message = obj.optString("message");

                                if (!error) {
                                    Toast.makeText(getContext(), "Profile completed!", Toast.LENGTH_SHORT).show();
                                    navigateToVerificationFragment(photoBase64);
                                } else {
                                    Toast.makeText(getContext(), "Error: " + message, Toast.LENGTH_LONG).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getContext(), "Parsing error", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToVerificationFragment(String photoBase64) {
        ProfilePictureVerification verificationFragment = new ProfilePictureVerification();
        Bundle bundle = new Bundle();
        bundle.putString("user_gender", getGender());
        bundle.putString("profile_photo_base64", photoBase64);
        verificationFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profileContainer, verificationFragment)
                .addToBackStack(null)
                .commit();
    }
}
