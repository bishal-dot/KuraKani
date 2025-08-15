package com.example.kurakani.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.R;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("KurakaniPrefs", MODE_PRIVATE);

            if (sharedPreferences.contains("auth_token")) {
                // User already logged in → go to HomePageActivity
                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                startActivity(intent);
            } else {
                // No token → go to AuthActivity (Login/Register)
                Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                startActivity(intent);
            }

            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DELAY);
    }
}
