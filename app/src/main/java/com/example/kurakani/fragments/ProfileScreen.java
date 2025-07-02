package com.example.kurakani.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.kurakani.R;

public class ProfileScreen extends Fragment {

    private ImageView settingIcon;

    public ProfileScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_profile_screen, container, false);

        // Find the settings icon
        settingIcon = view.findViewById(R.id.settingIcon);

        // Handle click → navigate to ProfileSetting fragment
        settingIcon.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new ProfileSetting());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}