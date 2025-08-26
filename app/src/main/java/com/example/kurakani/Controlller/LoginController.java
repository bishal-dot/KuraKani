package com.example.kurakani.Controlller;

import android.content.SharedPreferences;
import android.util.Log;
import android.util.Patterns;

import com.example.kurakani.model.LoginModel;
import com.example.kurakani.model.LoginResponse;
import com.example.kurakani.model.User;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginController {

    private final LoginActivity view;
    private boolean rememberMe;

    public LoginController(LoginActivity view) {
        this.view = view;
    }

    public void validateFields(boolean rememberMe) {
        this.rememberMe = rememberMe;
        String email = view.getUsername();
        String password = view.getPassword();

        if (email.isEmpty()) {
            view.setUsernameError("Email cannot be empty");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.setUsernameError("Enter a valid email");
            return;
        }
        if (password.isEmpty()) {
            view.setPasswordError("Password cannot be empty");
            return;
        }
        if (password.length() < 6) {
            view.setPasswordError("Password must be at least 6 characters");
            return;
        }

        loginUser(new LoginModel(email, password));
    }

    private void loginUser(LoginModel model) {
        ApiService apiService = RetrofitClient.getInstance(view).create(ApiService.class);
        apiService.loginUser(model.getEmail(), model.getPassword()).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    LoginResponse loginResponse = response.body();

                    User user = loginResponse.getUser();
                    String token = loginResponse.getToken();

                    SharedPreferences prefs = view.getSharedPreferences("KurakaniPrefs", android.content.Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("auth_token", token);

                    if (rememberMe) {
                        editor.putString("saved_email", model.getEmail());
                        editor.putString("saved_password", model.getPassword());
                        editor.putBoolean("remember_me", true);
                    } else {
                        editor.remove("saved_email");
                        editor.remove("saved_password");
                        editor.putBoolean("remember_me", false);
                    }
                    editor.apply();

                    RetrofitClient.resetClient();

                    // ✅ FIX: check from LoginResponse, not User
                    if (!loginResponse.isProfileComplete()) {
                        view.openProfileSetupFragment();
                    } else {
                        view.showHomePage();
                    }

                } else {
                    String errorMsg = "Login failed";
                    if (response.body() != null) {
                        errorMsg = response.body().getMessage();
                    } else {
                        errorMsg = "Error code: " + response.code();
                    }
                    view.showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                view.showError("Network error: " + t.getMessage());
                Log.e("LoginController", "Failure", t);
            }
        });
    }
}
