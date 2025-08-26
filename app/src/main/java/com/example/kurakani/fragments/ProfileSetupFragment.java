package com.example.kurakani.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
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
import com.example.kurakani.views.HomePageActivity;
import com.example.kurakani.views.LoginActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            if (validateInputs()) sendProfileToServer();
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

    private String encodeToBase64(Bitmap bitmap, int quality) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);
    }

    private void sendProfileToServer() {
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
                encodeToBase64(getProfilePhoto(), 70),
                getPurpose(),
                getJob(),
                interestsList,
                getEducation(),
                getBio()
        );

        Log.d("PROFILE_REQUEST", new Gson().toJson(request));

        // ✅ Get token safely
        Context ctx = getContext() != null ? getContext() : getActivity();
        if (ctx == null) {
            Toast.makeText(getContext(), "Context not available", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = ctx.getSharedPreferences("KurakaniPrefs", Activity.MODE_PRIVATE)
                .getString("auth_token", null);

        Log.d("TOKEN_DEBUG", "Token: " + token);
        if (token == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_LONG).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient(ctx).create(ApiService.class);

        apiService.completeProfile(request)
                .enqueue(new Callback<ProfileResponse>() {
                    @Override
                    public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ProfileResponse body = response.body();
                            if (!body.error) {
                                saveToPrefs(body.user);
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Error: " + body.message, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
                                Log.e("API_ERROR", "Server error: " + response.code() + ", " + errorBody);
                                Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                            } catch (Exception e) { e.printStackTrace(); }
                        }
                    }

                    @Override
                    public void onFailure(Call<ProfileResponse> call, Throwable t) {
                        Log.e("API_FAILURE", t.getMessage(), t);
                        Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveToPrefs(ProfileResponse.User user) {
        if (getActivity() != null && user != null) {
            String interestsStr = "";
            if (user.interests != null && !user.interests.isEmpty()) {
                interestsStr = String.join(",", user.interests);
            }

            getActivity().getSharedPreferences("user_profile", Activity.MODE_PRIVATE).edit()
                    .putString("fullname", user.fullname)
                    .putInt("age", user.age != null ? user.age : 0)
                    .putString("gender", user.gender)
                    .putString("purpose", user.purpose != null ? user.purpose : "")
                    .putString("job", user.job != null ? user.job : "")
                    .putString("interests", interestsStr)
                    .putString("education", user.education != null ? user.education : "")
                    .putString("about", user.bio != null ? user.bio : (user.about != null ? user.about : ""))
                    .putString("profile_photo", user.profile)
                    .apply();
        }
    }
}
