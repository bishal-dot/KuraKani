package com.example.kurakani;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MoreInfo extends AppCompatActivity {

    FrameLayout moreInfo;

    Button btnNext, btnBack;

    int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_info);

        moreInfo = findViewById(R.id.moreInfo);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);

        loadFragment(new JobTitle());
        btnNext.setOnClickListener(v -> {
            if (currentPage == 0) loadFragment(new Interests());
            else if (currentPage == 1) loadFragment(new Aboutme());
            else if (currentPage == 2) {
                Intent intent = new Intent(MoreInfo.this, HomePageActivity.class);
                startActivity(intent);
            }
            else return;
            currentPage++;
        });

        btnBack.setOnClickListener(v -> {
            if (currentPage == 2) loadFragment(new Interests());
            else if (currentPage == 1) loadFragment(new JobTitle());
            else return;
            currentPage--;
        });

    }

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.moreInfo, fragment)
                .commit();
    }
}