package com.example.kurakani.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.kurakani.R;
import com.example.kurakani.fragments.ProfilePicture;
import com.example.kurakani.fragments.ProfileSetup;
import com.example.kurakani.fragments.Purpose;
import com.google.android.material.button.MaterialButton;

public class ProfileSetupActivity extends AppCompatActivity {

    FrameLayout profileContainer;

    MaterialButton backBtn, nextBtn;
    int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        profileContainer = findViewById(R.id.profileContainer);
        nextBtn = findViewById(R.id.nextBtn);
        backBtn = findViewById(R.id.backBtn);

        loadFragment(new ProfileSetup());
        nextBtn.setOnClickListener(view -> {
            if (currentPage == 0) loadFragment(new ProfilePicture());
            else if (currentPage == 1) loadFragment(new Purpose());
            else if (currentPage == 2) {
                Intent intent = new Intent(ProfileSetupActivity.this, GettingStarted.class);
                startActivity(intent);
            }
            else return;
            currentPage++;
        });

        backBtn.setOnClickListener(view -> {
            if (currentPage == 2) loadFragment(new ProfilePicture());
            else if (currentPage == 1) loadFragment(new ProfileSetup());
            else return;
            currentPage--;
        });

    }
    private  void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profileContainer, fragment)
                .commit();
    }
}