package com.example.kurakani.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.kurakani.R;
import com.example.kurakani.fragments.ChatFragment;
import com.example.kurakani.fragments.HomePageFragment;
import com.example.kurakani.fragments.ProfileExpanded;
import com.example.kurakani.fragments.ProfileMatches;
import com.example.kurakani.fragments.ProfileSetting;
import com.example.kurakani.fragments.ProfileSetupFragment;

public class HomePageActivity extends AppCompatActivity {

    private ImageView v_profileSetting, v_matchIcon, v_homePage, v_chat;

    private int currentTab = R.id.v_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        findViews();

            // Handle notification intent (opens ProfileExpanded if matched_user_id exists)
        handleIntent(getIntent());

        // Bottom nav click listeners
        v_homePage.setOnClickListener(v -> switchBottomTab(R.id.v_home, new HomePageFragment()));
        v_matchIcon.setOnClickListener(v -> switchBottomTab(R.id.v_match, new ProfileMatches()));
        v_chat.setOnClickListener(v -> switchBottomTab(R.id.v_chat, new ChatFragment()));
        v_profileSetting.setOnClickListener(v -> switchBottomTab(R.id.v_profileSetting, new ProfileSetting()));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        boolean openProfileSetup = intent.getBooleanExtra("open_profile_setup", false);
        int matchedUserId = intent.getIntExtra("matched_user_id", -1);

        if (matchedUserId != -1) {
            // Open ProfileExpanded fragment for the matched user
            ProfileExpanded profileFragment = ProfileExpanded.newInstance(matchedUserId);
            switchFragment(profileFragment, true);
        } else if (openProfileSetup) {
            // Open ProfileSetupFragment after signup
            switchFragment(new ProfileSetupFragment(), false);
            highlightTab(null); // no tab highlighted
        } else if (getSupportFragmentManager().getFragments().isEmpty()) {
            // Default: HomePageFragment
            switchFragment(new HomePageFragment(), false);
            highlightTab(v_homePage);
            currentTab = R.id.v_home;
        }
    }

    private void findViews() {
        v_profileSetting = findViewById(R.id.v_profileSetting);
        v_matchIcon = findViewById(R.id.v_match);
        v_homePage = findViewById(R.id.v_home);
        v_chat = findViewById(R.id.v_chat);
    }

    private void switchBottomTab(int tabId, Fragment fragment) {
        if (currentTab == tabId) return;
        switchFragment(fragment, false);
        highlightTab(findViewById(tabId));
        currentTab = tabId;
    }

    private void switchFragment(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment);

        if (addToBackStack) transaction.addToBackStack(null);

        transaction.commit();
    }

    private void highlightTab(ImageView selectedTab) {
        v_homePage.setColorFilter(getResources().getColor(R.color.text_dark));
        v_matchIcon.setColorFilter(getResources().getColor(R.color.text_dark));
        v_chat.setColorFilter(getResources().getColor(R.color.text_dark));
        v_profileSetting.setColorFilter(getResources().getColor(R.color.text_dark));

        if (selectedTab != null) {
            selectedTab.setColorFilter(getResources().getColor(R.color.primary_color));
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}
