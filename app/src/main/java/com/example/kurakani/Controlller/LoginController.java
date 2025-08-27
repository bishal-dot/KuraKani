package com.example.kurakani.Controlller;

import android.content.SharedPreferences;
import android.util.Log;
import android.util.Patterns;

import com.example.kurakani.model.LoginModel;
import com.example.kurakani.model.LoginResponse;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.network.ApiService;
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
            view.setPasswordError("Password cannot be empty!");
            return;
        }
        if (password.length() < 6) {
            view.setPasswordError("Password must be at least 6 characters");
            return;
        }

        LoginModel model = new LoginModel(email, password);
        loginUser(model);
    }

    private void loginUser(LoginModel model) {
        ApiService apiService = RetrofitClient.getClient(view).create(ApiService.class);

        Call<LoginResponse> call = apiService.loginUser(model.getEmail(), model.getPassword());

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("LoginResponse", "Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (!loginResponse.isError()) {
                        String token = loginResponse.getToken();
                        LoginResponse.User user = loginResponse.getUser();

                        if (user == null) {
                            view.showError("Invalid user data received");
                            return;
                        }

                        int userId = user.getId();
                        boolean profileComplete = user.isProfileComplete(); // <-- use API field

                        saveLoginPreferences(token, userId, model, profileComplete);
                        RetrofitClient.resetClient();

                        if (profileComplete) {
                            view.showHomePage(
                                    rememberMe,
                                    token,
                                    userId,
                                    user.getEmail(),
                                    model.getPassword()
                            );
                        } else {
                            view.showProfileSetup(token, userId, user.getEmail());
                        }

                    } else {
                        String errorMsg = loginResponse.getMessage() != null
                                ? loginResponse.getMessage()
                                : "Login failed";
                        view.showError(errorMsg);
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                handleFailure(t);
            }
        });
    }

    private void saveLoginPreferences(String token, int userId, LoginModel model, boolean profileComplete) {
        SharedPreferences prefs = view.getSharedPreferences("KurakaniPrefs", android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("auth_token", token);
        editor.putInt("user_id", userId);
        editor.putBoolean("profile_complete", profileComplete);

        if (rememberMe) {
            editor.putBoolean("remember_me", true);
            editor.putString("saved_email", model.getEmail());
            editor.putString("saved_password", model.getPassword());
        } else {
            editor.putBoolean("remember_me", false);
            editor.remove("saved_email");
            editor.remove("saved_password");
        }
        editor.apply();
    }

    private void handleErrorResponse(Response<LoginResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
            Log.e("LoginErrorBody", errorBody);
            view.showError("Server error (" + response.code() + "): " + errorBody);
        } catch (Exception e) {
            view.showError("Login failed: Invalid response");
            Log.e("LoginErrorParse", "Failed to parse error body", e);
        }
    }

    private void handleFailure(Throwable t) {
        if (t instanceof java.io.IOException) {
            view.showError("Network error: Please check your connection");
        } else {
            view.showError("Unexpected error: " + t.getMessage());
        }
        Log.e("LoginFailure", "Login request failed", t);
    }
}