package com.example.kurakani.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.Controlller.LoginController;
import com.example.kurakani.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private CheckBox rmbrMe;
    private LoginController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.usernameField);
        etPassword = findViewById(R.id.passwordField);
        btnLogin = findViewById(R.id.loginButton);
        rmbrMe = findViewById(R.id.rmbrme);

        controller = new LoginController(this);

        // Prefill saved credentials
        SharedPreferences prefs = getSharedPreferences("KurakaniPrefs", MODE_PRIVATE);
        if (prefs.getBoolean("remember_me", false)) {
            etEmail.setText(prefs.getString("saved_email", ""));
            etPassword.setText(prefs.getString("saved_password", ""));
            rmbrMe.setChecked(true);
        }

        btnLogin.setOnClickListener(v -> controller.validateFields(rmbrMe.isChecked()));

        findViewById(R.id.goback).setOnClickListener(v -> finish());
    }

    public String getUsername() { return etEmail.getText().toString().trim(); }
    public String getPassword() { return etPassword.getText().toString(); }

    public void showError(String msg) { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show(); }
    public void setUsernameError(String msg) { etEmail.setError(msg); }
    public void setPasswordError(String msg) { etPassword.setError(msg); }

    public void openProfileSetupFragment() {
        Toast.makeText(this, "Redirecting to Profile Setup", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, ProfileSetupActivity.class));
        finish();
    }

    public void showHomePage() {
        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
