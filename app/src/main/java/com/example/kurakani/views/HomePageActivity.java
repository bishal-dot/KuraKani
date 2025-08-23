package com.example.kurakani.views;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kurakani.R;
import com.example.kurakani.fragments.ChatFragment;
import com.example.kurakani.fragments.HomePageFragment;
import com.example.kurakani.fragments.ProfileMatches;
import com.example.kurakani.fragments.ProfileSetting;

public class HomePageActivity extends AppCompatActivity {

    private ImageView v_profileSetting, v_matchIcon, v_homePage, v_chat;

    // Track the currently visible bottom tab fragment
    private int currentTab = R.id.v_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        findViews();

        // Load default fragment
        if (savedInstanceState == null) {
            switchFragment(new HomePageFragment(), false);
            highlightTab(v_homePage);
            currentTab = R.id.v_home;
        }

        // Bottom nav click listeners
        v_homePage.setOnClickListener(v -> switchBottomTab(R.id.v_home, new HomePageFragment()));
        v_matchIcon.setOnClickListener(v -> switchBottomTab(R.id.v_match, new ProfileMatches()));
        v_chat.setOnClickListener(v -> switchBottomTab(R.id.v_chat, new ChatFragment()));
        v_profileSetting.setOnClickListener(v -> switchBottomTab(R.id.v_profileSetting, new ProfileSetting()));
    }

    private void findViews() {
        v_profileSetting = findViewById(R.id.v_profileSetting);
        v_matchIcon = findViewById(R.id.v_match);
        v_homePage = findViewById(R.id.v_home);
        v_chat = findViewById(R.id.v_chat);
    }

    private void switchBottomTab(int tabId, androidx.fragment.app.Fragment fragment) {
        if (currentTab == tabId) return; // Already selected
        switchFragment(fragment, false); // false: do not add bottom tab fragments to back stack
        highlightTab(findViewById(tabId));
        currentTab = tabId;
    }

    private void switchFragment(androidx.fragment.app.Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment);

        if (addToBackStack) transaction.addToBackStack(null);

        transaction.commit();
    }

    private void highlightTab(View selectedTab) {
        // Reset all icons to default
        v_homePage.setColorFilter(getResources().getColor(R.color.text_dark));
        v_matchIcon.setColorFilter(getResources().getColor(R.color.text_dark));
        v_chat.setColorFilter(getResources().getColor(R.color.text_dark));
        v_profileSetting.setColorFilter(getResources().getColor(R.color.text_dark));

        // Highlight selected
        if (selectedTab instanceof ImageView) {
            ((ImageView) selectedTab).setColorFilter(getResources().getColor(R.color.primary_color));
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // If detail fragment is open (like EditProfile), pop it
            getSupportFragmentManager().popBackStack();
        } else {
            // No detail fragment, finish activity
            super.onBackPressed();
        }
    }
}
