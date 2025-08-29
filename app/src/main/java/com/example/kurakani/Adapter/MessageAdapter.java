package com.example.kurakani.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kurakani.R;
import com.example.kurakani.model.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final List<Message> messages;
    private final int currentUserId;

    public MessageAdapter(List<Message> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        // --- Message bubble content handling ---
        boolean hasText = message.getMessage() != null && !message.getMessage().isEmpty();
        boolean hasImage = message.getImageUrl() != null
                && !message.getImageUrl().isEmpty()
                && !message.getImageUrl().equals(Message.getDefaultImageUrl());

        if (hasText) {
            holder.tvMessage.setText(message.getMessage());
            holder.tvMessage.setVisibility(View.VISIBLE);
        } else {
            holder.tvMessage.setVisibility(View.GONE);
        }

        if (hasImage) {
            holder.ivMessageImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(message.getImageUrl())
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(holder.ivMessageImage);
        } else {
            holder.ivMessageImage.setVisibility(View.GONE);
        }

        // --- ConstraintLayout alignment ---
        ConstraintLayout layout = holder.messageContainer;
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        if (message.getSenderId() == currentUserId) {
            // Outgoing message (me)
            holder.ivMessageUserProfile.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(message.getSenderProfileUrl())
                    .circleCrop()
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(holder.ivMessageUserProfile);

            set.clear(R.id.messageBubbleContainer, ConstraintSet.START);
            set.connect(R.id.messageBubbleContainer, ConstraintSet.END,
                    R.id.ivMessageUserProfile, ConstraintSet.START, 8);
            set.clear(R.id.ivMessageUserProfile, ConstraintSet.START);
            set.connect(R.id.ivMessageUserProfile, ConstraintSet.END,
                    ConstraintSet.PARENT_ID, ConstraintSet.END, 0);

            holder.bubbleContent.setBackgroundResource(R.drawable.bg_bubble_sent);
        } else {
            // Incoming message (other)
            holder.ivMessageUserProfile.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(message.getSenderProfileUrl())
                    .circleCrop()
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(holder.ivMessageUserProfile);

            set.clear(R.id.messageBubbleContainer, ConstraintSet.END);
            set.connect(R.id.messageBubbleContainer, ConstraintSet.START,
                    R.id.ivMessageUserProfile, ConstraintSet.END, 8);
            set.clear(R.id.ivMessageUserProfile, ConstraintSet.END);
            set.connect(R.id.ivMessageUserProfile, ConstraintSet.START,
                    ConstraintSet.PARENT_ID, ConstraintSet.START, 0);

            holder.bubbleContent.setBackgroundResource(R.drawable.bg_bubble_received);
        }

        set.applyTo(layout);

        // Timestamp
        holder.tvTimestamp.setText(generateFriendlyTime());
        holder.tvTimestamp.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private String generateFriendlyTime() {
        Calendar now = Calendar.getInstance();
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return "Today, " + timeFormat.format(now.getTime());
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTimestamp;
        LinearLayout bubbleContent;
        ImageView ivMessageImage, ivMessageUserProfile;
        ConstraintLayout messageContainer;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContainer = itemView.findViewById(R.id.messageContainer);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            bubbleContent = itemView.findViewById(R.id.bubbleContent);
            ivMessageImage = itemView.findViewById(R.id.ivMessageImage);
            ivMessageUserProfile = itemView.findViewById(R.id.ivMessageUserProfile);
        }
    }

    public void updateMessages(List<Message> newMessages) {
        for (Message newMsg : newMessages) {
            boolean exists = false;
            for (Message oldMsg : messages) {
                if (oldMsg.getId() == newMsg.getId()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                messages.add(newMsg);
                notifyItemInserted(messages.size() - 1);
            }
        }
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }
}
