package com.example.kurakani.views;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 4000; // 4 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // ensure this XML exists

        // ✅ Fetch FCM token
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("MainActivity", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String fcmToken = task.getResult();
                        Log.d("MainActivity", "FCM Token: " + fcmToken);

                        // (Optional) Save it locally if needed
                        SharedPreferences prefs = getSharedPreferences("KurakaniPrefs", MODE_PRIVATE);
                        prefs.edit().putString("fcm_token", fcmToken).apply();

                        // TODO: send fcmToken to your backend with user authentication if required
                    }
                });

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
