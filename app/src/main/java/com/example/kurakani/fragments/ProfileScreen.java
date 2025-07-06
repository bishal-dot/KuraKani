package com.example.kurakani.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.kurakani.R;

public class ProfileScreen extends Fragment {

    private TextView backButton, editProfileButton;


    public ProfileScreen() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.fragment_profile_screen, container, false);



        // Find the back button
        backButton = view.findViewById(R.id.backButton);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        editProfileButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new EditProfileFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}