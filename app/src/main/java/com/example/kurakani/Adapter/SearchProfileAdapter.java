package com.example.kurakani.Adapter;

import android.content.Context;
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
import com.example.kurakani.model.ProfileResponse;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class SearchProfileAdapter extends RecyclerView.Adapter<SearchProfileAdapter.ProfileViewHolder> {

    private Context context;
    private List<ProfileResponse.User> profileList;

    public SearchProfileAdapter(Context context, List<ProfileResponse.User> profileList) {
        this.context = context;
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
        ProfileResponse.User profile = profileList.get(position);

        holder.userName.setText((profile.fullname != null ? profile.fullname : "Unknown")
                + ", " + (profile.age != null ? profile.age : "-"));

        Glide.with(holder.itemView.getContext())
                .load(profile.profile != null ? profile.profile : "")
                .placeholder(R.drawable.default_avatar)
                .into(holder.profileImage);

        holder.interestsChipGroup.removeAllViews();
        if (profile.interests != null && !profile.interests.isEmpty()) {
            for (String interest : profile.interests) {
                Chip chip = new Chip(holder.itemView.getContext());
                chip.setText(interest);
                chip.setCheckable(false);
                chip.setClickable(false);
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
