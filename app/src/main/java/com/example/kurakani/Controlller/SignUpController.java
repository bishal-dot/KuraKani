package com.example.kurakani.Controlller;

import android.util.Log;
import com.example.kurakani.model.SignupRequest;
import com.example.kurakani.model.SignupResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.SignupActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpController {

    private final SignupActivity view;

    public SignUpController(SignupActivity view) {
        this.view = view;
    }

    public void validateFields() {
        String username = view.getName();
        String email = view.getEmail();
        String password = view.getPassword();
        String confirmPassword = view.getConfirmPassword();

        view.clearErrors();

        if (username.isEmpty()) { view.setNameError("Username can't be blank."); return; }
        if (email.isEmpty()) { view.setEmailError("Email can't be blank."); return; }
        if (password.isEmpty()) { view.setPasswordError("Password cannot be empty!"); return; }
        if (password.length() < 6) { view.setPasswordError("Password must be at least 6 characters"); return; }
        if (!password.equals(confirmPassword)) { view.setConfirmPasswordError("Passwords don't match"); return; }
        if (!view.isTermsChecked()) { view.showError("Accept terms & conditions."); return; }

        SignupRequest request = new SignupRequest(username, email, password);
        createAccount(request);
    }

    private void createAccount(SignupRequest request) {
        ApiService apiService = RetrofitClient.getInstance(view).create(ApiService.class);
        apiService.registerUser(request).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isError()) {
                    SignupResponse.User user = response.body().getUser();
                    String token = response.body().getToken();
                    if (user == null || token == null) {
                        view.showError("Invalid user data received");
                        return;
                    }

                    // Save token in SharedPreferences
                    view.getSharedPreferences("user_profile", view.MODE_PRIVATE)
                            .edit()
                            .putString("signup_token", token)
                            .putString("user_email", user.getEmail())
                            .putInt("user_id", user.getId())
                            .apply();

                    // Navigate to profile creation
                    view.goToProfileSetup(user.getId(), user.getEmail());

                    // Handle null profile_complete safely
                    boolean profileComplete = user.isProfileComplete() != null && user.isProfileComplete();


                        // Navigate to profile creation
                        view.goToProfileSetup(user.getId(), user.getEmail());

                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "Signup failed: " + response.code();
                    view.showError(msg);
                    Log.e("SignUpController", msg);
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                view.showError("Network error: " + t.getMessage());
                Log.e("SignUpController", "Failure", t);
            }
        });
    }
}
