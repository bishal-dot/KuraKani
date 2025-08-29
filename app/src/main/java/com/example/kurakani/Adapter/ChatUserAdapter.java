package com.example.kurakani.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kurakani.R;
import com.example.kurakani.model.ProfileResponse;

import java.util.ArrayList;
import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ChatUserViewHolder> {

    private final List<ProfileResponse.User> userList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ProfileResponse.User user);
    }

    public ChatUserAdapter(List<ProfileResponse.User> userList, OnItemClickListener listener) {
        this.userList = userList != null ? userList : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_user, parent, false);
        return new ChatUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUserViewHolder holder, int position) {
        ProfileResponse.User user = userList.get(position);

        holder.tvUserName.setText(user.fullname != null ? user.fullname : "Unknown");

        if (user.photos != null && !user.photos.isEmpty() && user.photos.get(0).url != null) {
            Glide.with(holder.itemView.getContext())
                    .load(user.photos.get(0).url)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .circleCrop()
                    .into(holder.imgUserAvatar);
        } else {
            holder.imgUserAvatar.setImageResource(R.drawable.default_avatar);
        }

        int unreadCount = user.getUnreadCount();
        if (unreadCount > 0) {
            holder.tvUnreadIndicator.setVisibility(View.VISIBLE);
            holder.tvUnreadIndicator.setText(String.valueOf(unreadCount));
        } else {
            holder.tvUnreadIndicator.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<ProfileResponse.User> newList) {
        userList.clear();
        if (newList != null) userList.addAll(newList);
        notifyDataSetChanged();
    }

    static class ChatUserViewHolder extends RecyclerView.ViewHolder {
        final TextView tvUserName;
        final ImageView imgUserAvatar;
        final TextView tvUnreadIndicator;

        public ChatUserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            imgUserAvatar = itemView.findViewById(R.id.imgUserAvatar);
            tvUnreadIndicator = itemView.findViewById(R.id.tvUnreadIndicator);
        }
    }
}
