package com.example.kurakani.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.AuthActivity;
import com.example.kurakani.HomePageActivity;
import com.example.kurakani.ProfileSetupActivity;
import com.example.kurakani.R;
import com.example.kurakani.controller.LoginController;
import com.example.kurakani.fragments.HomePage;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class LoginActivity extends AppCompatActivity {

    TextView tvFrgtPassword ,goBack;
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
        tilPassword = findViewById(R.id.tilPassword);
        tilUsername = findViewById(R.id.tilUsername);

        tvFrgtPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        goBack.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AuthActivity.class);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        loginButton.setOnClickListener(v -> {
            controller.validateFields();
        });
    }

    public void showError(String message){
        Snackbar.make(loginButton.getRootView(), message, Snackbar.LENGTH_LONG).show();
    }
    public String getUsername(){
        return username.getText().toString();
    }
    public String getPassword(){
        return password.getText().toString();
    }
    public void setUsernameError(String message){
        tilUsername.setError(message);
    }
    public void setPasswordError(String message) {
        tilPassword.setError(message);
    }

    public void showHomePage(){
        Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
        startActivity(intent);
        finish();
    }
}