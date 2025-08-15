package com.example.kurakani.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.R;
import com.example.kurakani.Controlller.LoginController;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

    public void showHomePage(boolean remember, String token) {
        // token and remember are handled in controller already for storage
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }
}
