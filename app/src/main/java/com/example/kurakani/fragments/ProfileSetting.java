package com.example.kurakani.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kurakani.R;
import com.google.android.material.card.MaterialCardView;

public class ProfileSetting extends Fragment {

    TextView tvProfileView, tvPrivacyPolicy;
    CardView paymentMethod;

    public ProfileSetting() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_setting, container, false);

        tvProfileView = view.findViewById(R.id.tvProfileView);
        tvPrivacyPolicy = view.findViewById(R.id.tvPrivacyPolicy);
        paymentMethod = view.findViewById(R.id.paymentMethod);

        tvProfileView.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new ProfileScreen());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        tvPrivacyPolicy.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new PrivacyPolicyFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        paymentMethod.setOnClickListener(v -> {
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new PaymentMethodFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return view;

    }
}