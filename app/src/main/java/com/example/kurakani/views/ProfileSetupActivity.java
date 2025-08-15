package com.example.kurakani.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.R;
import com.example.kurakani.fragments.ProfileSetupFragment;

public class ProfileSetupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        // Load the ProfileSetupFragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profileContainer, new ProfileSetupFragment())
                .commit();
    }
}
