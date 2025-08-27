package com.example.kurakani.Adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.R;
import com.example.kurakani.model.Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;
    private int currentUserId;

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

        // Set message text
        String messageText = message.getMessage() != null ? message.getMessage() : "";
        holder.tvMessage.setText(messageText);

        // Align message bubble
        if (message.getSenderId() == currentUserId) {
            holder.tvMessage.setBackgroundResource(R.drawable.bg_bubble_sent);
            holder.bubbleContainer.setGravity(Gravity.END);
        } else {
            holder.tvMessage.setBackgroundResource(R.drawable.bg_bubble_received);
            holder.bubbleContainer.setGravity(Gravity.START);
        }

        // Always generate current friendly timestamp
        holder.tvTimestamp.setText(generateFriendlyTime());
        holder.tvTimestamp.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Generate a user-friendly time for display using current system time:
     * Today, Yesterday, or DayOfWeek + hh:mm a
     */
    private String generateFriendlyTime() {
        Calendar now = Calendar.getInstance();

        // Time part
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String timePart = timeFormat.format(now.getTime());

        // Today
        return "Today, " + timePart;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTimestamp;
        LinearLayout bubbleContainer;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            bubbleContainer = itemView.findViewById(R.id.messageBubbleContainer);
        }
    }

    // Merge new messages by ID (no duplicates, no disappearing)
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

    // Add single message (useful when sending a new message)
    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }
}
