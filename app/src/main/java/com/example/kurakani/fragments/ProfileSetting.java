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
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSetting extends Fragment {

    private TextView tvUserName, tvUserEmail, tvProfileView;
    private ImageView profilePicture;
    private CardView paymentMethod;
    private MaterialButton btnLogout;

    public ProfileSetting() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile_setting, container, false);

        // Bind views
        tvUserName = view.findViewById(R.id.userName);
        tvUserEmail = view.findViewById(R.id.userEmail);
        profilePicture = view.findViewById(R.id.profilePicture);
        paymentMethod = view.findViewById(R.id.paymentMethod);
        btnLogout = view.findViewById(R.id.btnLogout);
        tvProfileView = view.findViewById(R.id.tvProfileView);

        fetchProfile();


        // Open ProfileScreen
        tvProfileView.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfileScreen())
                    .addToBackStack(null)
                    .commit();
        });

        // Open PaymentMethodFragment
        paymentMethod.setOnClickListener(v -> {
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, new PaymentMethodFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Logout
        btnLogout.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void fetchProfile() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("KurakaniPrefs", Context.MODE_PRIVATE);
        String token = prefs.getString("auth_token", "");

        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "Token not found, please login again", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getClient(requireContext()).create(ApiService.class);

        apiService.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().error) {
                    ProfileResponse.User user = response.body().user;

                    if (user != null) {
                        tvUserName.setText(user.fullname != null && !user.fullname.isEmpty() ? user.fullname : user.username);
                        tvUserEmail.setText(user.email != null ? user.email : "-");

                        if (user.profile != null && !user.profile.isEmpty()) {
                            String imageUrl = "http://your-laravel-domain.com/storage/" + user.profile;
                            Glide.with(requireContext())
                                    .load(imageUrl)
                                    .placeholder(R.drawable.john)
                                    .error(R.drawable.john)
                                    .into(profilePicture);
                        }
                    }
                } else {
                    Log.e("ProfileSetting", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("ProfileSetting", "onFailure: ", t);
            }
        });
    }

    private void logoutUser() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("KurakaniPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean rememberMe = prefs.getBoolean("remember_me", false);
        String savedEmail = prefs.getString("saved_email", null);
        String savedPassword = prefs.getString("saved_password", null);

        editor.clear(); // clear all data

        // Restore remember me data if needed
        if (rememberMe && savedEmail != null && savedPassword != null) {
            editor.putBoolean("remember_me", true);
            editor.putString("saved_email", savedEmail);
            editor.putString("saved_password", savedPassword);
        }

        editor.apply();

        RetrofitClient.resetClient();

        Intent intent = new Intent(requireActivity(), AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
