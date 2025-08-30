package com.example.kurakani.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kurakani.R;
import com.example.kurakani.model.User;

import java.util.ArrayList;
import java.util.List;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ChatUserViewHolder> {

    private final List<User> userList;
    private final List<User> userListFull; // backup for search
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public ChatUserAdapter(List<User> userList, OnItemClickListener listener) {
        this.userList = userList != null ? userList : new ArrayList<>();
        this.userListFull = new ArrayList<>();
        if (userList != null) this.userListFull.addAll(userList);
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
        User user = userList.get(position);

        holder.tvUserName.setText(user.getFullname() != null ? user.getFullname() : "Unknown");

        if (user.getPhotos() != null && !user.getPhotos().isEmpty() && user.getPhotos().get(0).getUrl() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(user.getPhotos().get(0).getUrl())
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .circleCrop()
                    .into(holder.imgUserAvatar);
        } else {
            holder.imgUserAvatar.setImageResource(R.drawable.default_avatar);
        }

        int unreadCount = Math.max(0, user.getUnreadCount());
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

    public void updateList(List<User> newList) {
        userList.clear();
        if (newList != null) userList.addAll(newList);

        userListFull.clear();
        if (newList != null) userListFull.addAll(newList);

        Log.d("ChatUserAdapter", "Adapter updated with " + userList.size() + " users.");
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
    public List<User> getUser() {
        return userList; // assuming your adapter stores its data in 'userList'
    }
}