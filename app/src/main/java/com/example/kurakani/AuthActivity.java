package com.example.kurakani;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        MaterialButton phoneLogin = findViewById(R.id.phonelogin);
        phoneLogin.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, PhoneActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        Button signUpButton = findViewById(R.id.signupbtn);
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        Button loginButton = findViewById(R.id.loginbtn);
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}