package com.example.kurakani.Controlller;

import android.util.Log;

import com.example.kurakani.model.SignupRequest;
import com.example.kurakani.model.SignupResponse;
import com.example.kurakani.model.User;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.SignupActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpController {

    private final SignupActivity view;

    public SignUpController(SignupActivity view){
        this.view = view;
    }

    public void validateFields() {
        String username = view.getName();
        String email = view.getEmail();
        String password = view.getPassword();
        String confirmPassword = view.getConfirmPassword();

        view.setNameError(null);
        view.setEmailError(null);
        view.setPasswordError(null);
        view.setConfirmPasswordError(null);

        if (username.isEmpty()) { view.setNameError("Username can't be blank."); return; }
        if (email.isEmpty()) { view.setEmailError("Enter a valid email."); return; }
        if (password.isEmpty()) { view.setPasswordError("Password cannot be empty!"); return; }
        if (password.length() < 6) { view.setPasswordError("Password must be 6+ chars"); return; }
        if (!confirmPassword.equals(password)) { view.setConfirmPasswordError("Passwords don't match"); return; }
        if (!view.isTermsChecked()) { view.showError("Accept terms & conditions."); return; }

        User user = new User(username, email, password);
        createAccount(user);
    }

    public void createAccount(User model) {
        ApiService apiService = RetrofitClient.getInstance(view).create(ApiService.class);

        SignupRequest request = new SignupRequest(model.getName(), model.getEmail(), model.getPassword());

        apiService.registerUser(request).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().isError()) {
                        String token = response.body().getToken();
                        view.getSharedPreferences("KurakaniPrefs", view.MODE_PRIVATE)
                                .edit()
                                .putString("auth_token", token)
                                .apply();

                        // Move to profile setup
                        view.showProfileSetup();
                    } else {
                        view.showError("API error: " + response.body().getReason());
                        Log.e("SignUpController", "API error reason: " + response.body().getReason());
                    }
                } else {
                    String errorBody = "";
                    try {
                        if(response.errorBody() != null)
                            errorBody = response.errorBody().string();
                    } catch (Exception e) { errorBody = "Failed to parse errorBody"; }
                    Log.e("SignUpController", "Signup failed. Code: " + response.code() + " Error: " + errorBody);
                    view.showError("Signup failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                view.showError("Network error: " + t.getMessage());
                Log.e("SignUpController", "createAccount error", t);
            }
        });
    }
}
