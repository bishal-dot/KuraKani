package com.example.kurakani.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.kurakani.R;
import com.google.android.material.card.MaterialCardView;


public class ChatFragment extends Fragment {

    MaterialCardView ivProfileButton;

    public ChatFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        int otherUserId = 2;
        String otherName = "Alex";
        ivProfileButton = view.findViewById(R.id.ivProfileButton);
        ivProfileButton.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, IndividualChat.newInstance(otherUserId, otherName));
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}