package com.example.kurakani.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kurakani.R;

public class ProfileMatchDetail extends Fragment {

    private String name, bio;
    private int age, avatarResId;

    public static ProfileMatchDetail newInstance(String name, int age, String bio, int avatarResId) {
        ProfileMatchDetail fragment = new ProfileMatchDetail();
        Bundle args = new Bundle();
        args.putString("name", name);
        args.putInt("age", age);
        args.putString("bio", bio);
        args.putInt("avatar", avatarResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString("name");
            age = getArguments().getInt("age");
            bio = getArguments().getString("bio");
            avatarResId = getArguments().getInt("avatar");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_match_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.imageViewProfile);
        TextView nameText = view.findViewById(R.id.textViewName);
        TextView ageText = view.findViewById(R.id.textViewAge);
        TextView bioText = view.findViewById(R.id.textViewBio);

        nameText.setText(name);
        ageText.setText("Age: " + age);
        bioText.setText(bio);
        imageView.setImageResource(avatarResId);

    }
}
