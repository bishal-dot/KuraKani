package com.example.kurakani.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.R;
import com.google.android.material.button.MaterialButton;

public class AuthActivity extends AppCompatActivity {

    MaterialButton loginBtn, googleLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        findview();

        MaterialButton phoneLogin = findViewById(R.id.phonelogin);
        phoneLogin.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, com.example.kurakani.views.PhoneActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        Button signUpButton = findViewById(R.id.signupbtn);
        signUpButton.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, com.example.kurakani.views.SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, com.example.kurakani.views.LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        googleLogin.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, HomePageActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });


    }

    private void findview(){
        loginBtn = findViewById(R.id.loginbtn);
        googleLogin = findViewById(R.id.googlelogin);
    }
}