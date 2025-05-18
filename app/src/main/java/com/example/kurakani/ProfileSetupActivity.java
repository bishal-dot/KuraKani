package com.example.kurakani;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
            if (currentPage == 0) {
                loadFragment(new ProfileGender());
                currentPage = 1;
            }
        });

        backBtn.setOnClickListener(view -> {
            if (currentPage == 1) {
                loadFragment(new ProfileSetup());
                currentPage = 0;
            }
        });
//        nextBtn.setOnClickListener(view -> {
//            if (currentPage == 0) loadFragment(new ProfileGender());
////            else if (currentPage == 1) loadFragment(new ThirdFragment());
////            else return;
//            currentPage++;
//        });
////
//        backBtn.setOnClickListener(view -> {
//            if (currentPage == 2) loadFragment(new ProfileGender());
////            else if (currentPage == 1) loadFragment(new FirstFragment());
////            else return;
//            currentPage--;
//        });

    }
    private  void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profileContainer, fragment)
                .commit();
    }
}