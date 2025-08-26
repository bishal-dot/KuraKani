package com.example.kurakani.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import com.example.kurakani.R;
import com.example.kurakani.Controlller.LoginController;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView tvFrgtPassword, goBack;
    MaterialButton loginButton;
    CheckBox cbRmbrme;
    TextInputEditText username, password;
    TextInputLayout tilUsername, tilPassword;

    LoginController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        controller = new LoginController(this);

        tvFrgtPassword = findViewById(R.id.forgotPassword);
        goBack = findViewById(R.id.goback);
        loginButton = findViewById(R.id.loginButton);
        cbRmbrme = findViewById(R.id.rmbrme);
        username = findViewById(R.id.usernameField);
        password = findViewById(R.id.passwordField);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);

        tvFrgtPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        goBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        loginButton.setOnClickListener(v -> {
            clearErrors();
            boolean remember = cbRmbrme.isChecked();
            controller.validateFields(remember);
        });

        SharedPreferences prefs = getSharedPreferences("KurakaniPrefs", MODE_PRIVATE);
        boolean remember = prefs.getBoolean("remember_me", false);

        if (remember) {
            String savedEmail = prefs.getString("saved_email", "");
            String savedPassword = prefs.getString("saved_password", "");
            username.setText(savedEmail);
            password.setText(savedPassword);
            cbRmbrme.setChecked(true);
        }
    }

    private void clearErrors() {
        tilUsername.setError(null);
        tilPassword.setError(null);
    }

    public String getUsername() {
        return username.getText().toString().trim();
    }

    public String getPassword() {
        return password.getText().toString().trim();
    }

    public void setUsernameError(String message) {
        tilUsername.setError(message);
    }

    public void setPasswordError(String message) {
        tilPassword.setError(message);
    }

    public void showError(String message) {
        Snackbar.make(loginButton, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Navigate to HomePageActivity after successful login.
     * Save logged-in user info, token, and "remember me" data in SharedPreferences.
     *
     * @param remember  true if user checked "remember me"
     * @param token     API token from backend
     * @param userId    Logged-in user's ID
     * @param email     User email (for remember me)
     * @param password  User password (for remember me)
     */
    public void showHomePage(boolean remember, String token, int userId, String email, String password) {
        // Save user ID & token consistently
        SharedPreferences prefs = getSharedPreferences("KurakaniPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("user_id", userId);
        editor.putString("auth_token", token); // ✅ Consistent key

        if (remember) {
            editor.putBoolean("remember_me", true);
            editor.putString("saved_email", email);
            editor.putString("saved_password", password);
        } else {
            editor.putBoolean("remember_me", false);
            editor.remove("saved_email");
            editor.remove("saved_password");
        }
        editor.apply();

        // ✅ Always fetch auth_token with same key
        String fullToken = "Bearer " + prefs.getString("auth_token", "");

        // Update FCM token in backend
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        task.getException().printStackTrace();
                        return;
                    }
                    String fcmToken = task.getResult();

                    HashMap<String, String> body = new HashMap<>();
                    body.put("fcm_token", fcmToken);

                    ApiService api = RetrofitClient.getInstance(this).create(ApiService.class);
                    api.updateFcmToken(body).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (!response.isSuccessful()) {
                                System.out.println("⚠️ FCM token update failed: " + response.code());
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            t.printStackTrace();
                        }
                    });
                });

        // Navigate to HomePageActivity
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }

}
