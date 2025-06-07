package com.example.kurakani;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.fragments.ProfileScreen;
import com.example.kurakani.fragments.ProfileSetting;

public class HomePageActivity extends AppCompatActivity {

    ImageView v_profileSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        findview();

        v_profileSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new ProfileScreen())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
    private void findview(){
        v_profileSetting = findViewById(R.id.v_profileSetting);

    }


}