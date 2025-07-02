package com.example.kurakani;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.R;
import com.example.kurakani.model.StoryModel;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private List<StoryModel> storyList;
    private Context context;
    private OnStoryClickListener listener;

    // Interface for click handling
    public interface OnStoryClickListener {
        void onStoryClick(StoryModel story);
    }

    public StoryAdapter(Context context, List<StoryModel> storyList, OnStoryClickListener listener) {
        this.context = context;
        this.storyList = storyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        StoryModel story = storyList.get(position);
        holder.imageView.setImageResource(story.getImageResId());
        holder.nameView.setText(story.getName());

        holder.itemView.setOnClickListener(v -> listener.onStoryClick(story));
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nameView;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.storyImage);
            nameView = itemView.findViewById(R.id.storyName);
        }
    }
}