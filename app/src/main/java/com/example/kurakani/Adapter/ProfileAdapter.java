package com.example.kurakani.Adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kurakani.R;
import com.example.kurakani.viewmodel.ProfileModel;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<ProfileModel> profileList;

    public interface OnSwipeListener {
        void onMatch(ProfileModel profile);
        void onReject(ProfileModel profile);
    }

    public interface OnItemClickListener {
        void onItemClick(ProfileModel profile);
    }

    private OnItemClickListener itemClickListener;
    public OnSwipeListener swipeListener;

    public ProfileAdapter(List<ProfileModel> profiles, OnSwipeListener listener) {
        this.profileList = profiles;
        this.swipeListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile_card, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        ProfileModel profile = profileList.get(position);
        holder.tvName.setText(profile.getFullname() + ", " + profile.getAge());

        List<String> interests = profile.getInterests();
        holder.tvHobby.setText(
                (interests != null && !interests.isEmpty()) ?
                        TextUtils.join(", ", interests) : "No hobbies"
        );

        Glide.with(holder.itemView.getContext())
                .load(profile.getProfile())
                .placeholder(R.drawable.john)
                .into(holder.imgProfile);

        // Only top card visible
        if (position == 0) {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setAlpha(1f);
            holder.itemView.setScaleX(1f);
            holder.itemView.setScaleY(1f);
            holder.itemView.setTranslationY(0f);
        } else {
            holder.itemView.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public void removeTopItem() {
        if (!profileList.isEmpty()) {
            profileList.remove(0);
            notifyDataSetChanged(); // redraw so next card becomes visible
        }
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvHobby;
        ImageView imgProfile;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvHobby = itemView.findViewById(R.id.tvHobby);
            imgProfile = itemView.findViewById(R.id.imgProfile);

            itemView.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        itemClickListener.onItemClick(profileList.get(pos));
                    }
                }
            });
        }
    }
}
