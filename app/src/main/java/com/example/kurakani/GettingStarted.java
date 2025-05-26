package com.example.kurakani;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class GettingStarted extends AppCompatActivity {

    MaterialButton skipBtn, continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_getting_started);

        findViewById();
        continueBtn.setOnClickListener(v -> {
            Intent intent = new Intent(GettingStarted.this, MoreInfo.class);
            startActivity(intent);
        });

        skipBtn.setOnClickListener(v -> {
            Intent intent = new Intent(GettingStarted.this, HomePageActivity.class);
            startActivity(intent);
        });

    }

    private void findViewById(){
        skipBtn = findViewById(R.id.skipBtn);
        continueBtn = findViewById(R.id.continueBtn);

    }
}