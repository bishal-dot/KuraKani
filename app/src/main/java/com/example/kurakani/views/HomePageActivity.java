package com.example.kurakani.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.R;
import com.example.kurakani.fragments.ChatFragment;
import com.example.kurakani.fragments.HomePageFragment;
import com.example.kurakani.fragments.ProfileSetting;

public class HomePageActivity extends AppCompatActivity {

    ImageView v_profileSetting, v_matchIcon, v_homePage,v_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        findview();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new HomePageFragment())
                    .commit();
        }

        v_profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new ProfileSetting())
                        .addToBackStack(null)
                        .commit();
            }
        });
        v_matchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new ProfileMatches())
                        .addToBackStack(null)
                        .commit();
            }
        });

        v_homePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new HomePageFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
        v_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new ChatFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

    }
    private void findview(){
        v_profileSetting = findViewById(R.id.v_profileSetting);
        v_matchIcon = findViewById(R.id.v_match);
        v_homePage = findViewById(R.id.v_home);
        v_chat = findViewById(R.id.v_chat);
    }


}