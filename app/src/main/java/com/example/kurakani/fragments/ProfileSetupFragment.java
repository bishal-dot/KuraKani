package com.example.kurakani.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.kurakani.R;
import com.example.kurakani.model.ProfileRequest;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSetupFragment extends Fragment {

    private TextInputEditText etFullname, etAge, etPurpose, etInterests, etBio, etJob, etEducation;
    private RadioGroup rgGender;
    private Button btnSaveProfile;
    private boolean isRequestInProgress = false;
    private final String TAG = "ProfileSetupFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
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
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        btnSaveProfile.setOnClickListener(v -> attemptSaveProfile());

        return view;
    }

    private void attemptSaveProfile() {
        if (isRequestInProgress) return;
        if (!validateInputs()) return;

        isRequestInProgress = true;
        btnSaveProfile.setEnabled(false);
        sendProfileToServer();
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(getFullname())) {
            etFullname.setError("Full name required");
            return false;
        }
        if (getAge() < 18) {
            etAge.setError("You must be 18+");
            return false;
        }
        if (getGender() == null) {
            Toast.makeText(getContext(), "Select valid gender", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private String getFullname() { return etFullname.getText() != null ? etFullname.getText().toString().trim() : ""; }
    private int getAge() { try { return Integer.parseInt(etAge.getText().toString().trim()); } catch (Exception e) { return 0; } }
    private String getGender() {
        int id = rgGender.getCheckedRadioButtonId();
        if (id != -1) {
            RadioButton rb = rgGender.findViewById(id);
            if (rb != null && rb.getText() != null) return rb.getText().toString().toLowerCase();
        }
        return null;
    }
    private String getPurpose() { return etPurpose.getText() != null ? etPurpose.getText().toString().trim() : ""; }
    private String getInterests() { return etInterests.getText() != null ? etInterests.getText().toString().trim() : ""; }
    private String getBio() { return etBio.getText() != null ? etBio.getText().toString().trim() : ""; }
    private String getJob() { return etJob.getText() != null ? etJob.getText().toString().trim() : ""; }
    private String getEducation() { return etEducation.getText() != null ? etEducation.getText().toString().trim() : ""; }

    private void sendProfileToServer() {
        Context ctx = getContext();
        if (ctx == null) { resetState(); return; }

        String signupToken = ctx.getSharedPreferences("user_profile", Activity.MODE_PRIVATE)
                .getString("signup_token", null);

        if (signupToken == null) {
            Toast.makeText(ctx, "Signup token missing", Toast.LENGTH_SHORT).show();
            resetState();
            return;
        }

        List<String> interestsList = new ArrayList<>();
        String rawInterests = getInterests();
        if (!TextUtils.isEmpty(rawInterests)) {
            for (String s : rawInterests.split(",")) {
                String trimmed = s.trim();
                if (!trimmed.isEmpty()) interestsList.add(trimmed);
            }
        }

        ProfileRequest request = new ProfileRequest(
                getFullname(),
                getAge(),
                getGender(),
                getPurpose(),
                getJob(),
                interestsList.isEmpty() ? null : interestsList,
                getEducation(),
                getBio()
        );

        ApiService api = RetrofitClient.getClient(ctx).create(ApiService.class);
        api.createProfile("Bearer " + signupToken, request).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                resetState();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Profile created successfully!", Toast.LENGTH_SHORT).show();

                    // Save new auth token and remove signup token
                    ctx.getSharedPreferences("KurakaniPrefs", Activity.MODE_PRIVATE)
                            .edit()
                            .putString("auth_token", response.body().getToken())
                            .remove("signup_token")
                            .apply();

                    // Navigate to HomepageFragment
                    if (getActivity() != null) {
                        HomePageFragment homepageFragment = HomePageFragment.newInstance(response.body().getUser());
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentContainer, homepageFragment);
                        transaction.commit();
                    }
                } else {
                    try { Log.e(TAG, "Error body: " + response.errorBody().string()); } catch (Exception e) { e.printStackTrace(); }
                    Toast.makeText(getContext(), "Failed to create profile", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                resetState();
                Log.e(TAG, "Profile creation error", t);
                Toast.makeText(getContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetState() {
        isRequestInProgress = false;
        if (btnSaveProfile != null) btnSaveProfile.setEnabled(true);
    }
}
