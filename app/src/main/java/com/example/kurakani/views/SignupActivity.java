package com.example.kurakani.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.AuthActivity;
import com.example.kurakani.ProfileSetupActivity;
import com.example.kurakani.R;
import com.google.android.material.button.MaterialButton;

public class SignupActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        TextView goBack = findViewById(R.id.goback);
        goBack.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, AuthActivity.class);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        MaterialButton signUp = findViewById(R.id.btnSignup);
        signUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, ProfileSetupActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }
}