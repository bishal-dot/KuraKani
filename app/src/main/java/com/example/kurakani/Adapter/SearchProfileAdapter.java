package com.example.kurakani.Adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kurakani.R;
import com.example.kurakani.viewmodel.ProfileModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class SearchProfileAdapter extends RecyclerView.Adapter<SearchProfileAdapter.ProfileViewHolder> {

    private List<ProfileModel> profileList;

    public SearchProfileAdapter(List<ProfileModel> profileList) {
        this.profileList = profileList;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_profile_card, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        ProfileModel profile = profileList.get(position);

        holder.userName.setText(profile.getFullname() + ", " + profile.getAge());

        // Set profile image
        Glide.with(holder.itemView.getContext())
                .load(profile.getProfile())
                .placeholder(R.drawable.default_avatar)
                .into(holder.profileImage);

        // Add interest chips dynamically
        holder.interestsChipGroup.removeAllViews();
        List<String> interests = profile.getInterests();
        if (interests != null && !interests.isEmpty()) {
            for (String interest : interests) {
                Chip chip = new Chip(holder.itemView.getContext());
                chip.setText(interest);
                chip.setClickable(false);
                chip.setCheckable(false);
                holder.interestsChipGroup.addView(chip);
            }
        }
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView userName;
        ChipGroup interestsChipGroup;
        ImageButton messageButton, likeButton;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.profileImage);
            userName = itemView.findViewById(R.id.userName);
            interestsChipGroup = itemView.findViewById(R.id.interestsChipGroup);
            messageButton = itemView.findViewById(R.id.messageButton);
            likeButton = itemView.findViewById(R.id.likeButton);
        }
    }
}
