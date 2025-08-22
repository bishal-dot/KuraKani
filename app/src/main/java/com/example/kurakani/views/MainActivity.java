package com.example.kurakani.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.R;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // ensure this XML exists

        new Handler(getMainLooper()).postDelayed(() -> {
            SharedPreferences sharedPreferences = getSharedPreferences("KurakaniPrefs", MODE_PRIVATE);

            String token = sharedPreferences.getString("auth_token", null);
            Log.d("MainActivity", "Token: " + token);
            Intent intent;
            if (token != null && !token.isEmpty()) {
                intent = new Intent(MainActivity.this, HomePageActivity.class);
            } else {
                intent = new Intent(MainActivity.this, AuthActivity.class);
            }

            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DELAY);
    }
}
