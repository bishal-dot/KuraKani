package com.example.kurakani.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.kurakani.R;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.AuthActivity;
import com.example.kurakani.views.ChangePasswordActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSetting extends Fragment {

    private TextView tvUserName, tvUserEmail, tvProfileView, tvPrivacyPolicy, tvHelpCenter, tvChangePassword;
    private ImageView profilePicture;
    private CardView paymentMethod;
    private TextView btnLogout;

    private static final int EDIT_PROFILE_REQUEST = 1001;

    public ProfileSetting() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_setting, container, false);

        tvUserName = view.findViewById(R.id.userName);
        tvUserEmail = view.findViewById(R.id.userEmail);
        profilePicture = view.findViewById(R.id.profilePicture);
        paymentMethod = view.findViewById(R.id.paymentMethod);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvProfileView = view.findViewById(R.id.tvProfileView);
        tvPrivacyPolicy = view.findViewById(R.id.tvPrivacyPolicy);
        tvHelpCenter = view.findViewById(R.id.tvHelpCenter);
        tvChangePassword = view.findViewById(R.id.tvChangePassword);

        fetchProfile();

        tvProfileView.setOnClickListener(v -> {
            if (isAdded()) {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new ProfileScreen())
                        .addToBackStack(null)
                        .commit();
            }
        });

        tvChangePassword.setOnClickListener(v -> {
            if (isAdded()) {
                startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
            }
        });

        tvPrivacyPolicy.setOnClickListener(v -> {
            if (isAdded()) {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new PrivacyPolicyFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        paymentMethod.setOnClickListener(v -> {
            if (isAdded()) {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new PaymentMethodFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        tvHelpCenter.setOnClickListener(v -> {
            if (isAdded()) {
                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new HelpCenter())
                        .addToBackStack(null)
                        .commit();
            }
        });

        btnLogout.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void fetchProfile() {
        if (!isAdded()) return; // Safety check

        SharedPreferences prefs = getActivity().getSharedPreferences("KurakaniPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", "");

        if (token.isEmpty()) {
            if (isAdded() && getContext() != null) {
                Toast.makeText(getContext(), "Token not found, please login again", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        ApiService apiService = RetrofitClient.getClient(getContext()).create(ApiService.class);

        apiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (!isAdded()) return; // Fragment detached, do nothing

                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    ProfileResponse.User user = response.body().getUser();

                    if (user != null) {
                        tvUserName.setText(user.getFullname() != null ? user.getFullname() : user.getUsername());
                        tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "-");

                        String imageUrl = user.getProfile() != null ? user.getProfile().trim() : null;

                        if (getContext() != null) {
                            Glide.with(getContext())
                                    .load(imageUrl != null ? imageUrl + "?t=" + System.currentTimeMillis() : R.drawable.john)
                                    .placeholder(R.drawable.default_avatar)
                                    .error(R.drawable.default_avatar)
                                    .circleCrop()
                                    .into(profilePicture);
                        }
                    }
                } else {
                    Log.e("ProfileSetting", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("ProfileSetting", "onFailure: ", t);
            }
        });
    }

    private void logoutUser() {
        if (!isAdded()) return;

        SharedPreferences prefs = getActivity().getSharedPreferences("KurakaniPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean rememberMe = prefs.getBoolean("remember_me", false);
        String savedEmail = prefs.getString("saved_email", null);
        String savedPassword = prefs.getString("saved_password", null);

        editor.clear();

        if (rememberMe && savedEmail != null && savedPassword != null) {
            editor.putBoolean("remember_me", true);
            editor.putString("saved_email", savedEmail);
            editor.putString("saved_password", savedPassword);
        }

        editor.apply();

        RetrofitClient.resetClient();

        Intent intent = new Intent(getActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
