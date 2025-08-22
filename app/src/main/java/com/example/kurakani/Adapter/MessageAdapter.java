package com.example.kurakani.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kurakani.R;
import com.example.kurakani.model.Message;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_SENT = 1;
    private static final int VIEW_RECEIVED = 2;

    private final List<Message> data;
    private final int currentUserId;

    public MessageAdapter(List<Message> data, int currentUserId) {
        this.data = data;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemViewType(int position) {
        Message m = data.get(position);
        return (m.getSender_id() == currentUserId) ? VIEW_SENT : VIEW_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_SENT) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentVH(v);
        } else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message m = data.get(position);
        if (holder instanceof SentVH) {
            ((SentVH) holder).tv.setText(m.getMessage());
        } else if (holder instanceof ReceivedVH) {
            ((ReceivedVH) holder).tv.setText(m.getMessage());
        }
    }

    @Override
    public int getItemCount() { return data.size(); }

    public void replaceAll(List<Message> newData) {
        data.clear();
        data.addAll(newData);
        notifyDataSetChanged();
    }

    public void addOne(Message m) {
        data.add(m);
        notifyItemInserted(data.size() - 1);
    }

    static class SentVH extends RecyclerView.ViewHolder {
        TextView tv;
        SentVH(@NonNull View itemView) { super(itemView); tv = itemView.findViewById(R.id.tvMessageSent); }
    }
    static class ReceivedVH extends RecyclerView.ViewHolder {
        TextView tv;
        ReceivedVH(@NonNull View itemView) { super(itemView); tv = itemView.findViewById(R.id.tvMessageReceived); }
    }

}
