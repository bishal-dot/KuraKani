package com.example.kurakani;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
            if (currentPage == 0) loadFragment(new AgeSelect());
            else if (currentPage == 1) loadFragment(new ProfileGender());
            else if (currentPage == 2) loadFragment(new ProfilePicture());
            else if (currentPage == 3) loadFragment(new Location());
            else if (currentPage == 4) loadFragment(new Purpose());
            else if (currentPage == 5) loadFragment(new JobTitle());
            else if (currentPage == 6) loadFragment(new Education());
            else if (currentPage == 7) loadFragment(new Interests());
            else if (currentPage == 8) loadFragment(new Aboutme());
            else return;
            currentPage++;
        });

        backBtn.setOnClickListener(view -> {
            if (currentPage == 9) loadFragment(new Interests());
            else if (currentPage == 8) loadFragment(new Education());
            else if (currentPage == 7) loadFragment(new JobTitle());
            else if (currentPage == 6) loadFragment(new Purpose());
            else if (currentPage == 5) loadFragment(new Location());
            else if (currentPage == 4) loadFragment(new ProfilePicture());
            else if (currentPage == 3) loadFragment(new ProfileGender());
            else if (currentPage == 2) loadFragment(new AgeSelect());
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