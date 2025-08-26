package com.example.kurakani.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kurakani.R;

import java.util.List;

public class OtherProfilePhotosAdapter extends RecyclerView.Adapter<OtherProfilePhotosAdapter.PhotoViewHolder> {

    private final Context context;
    private final List<String> photos;

    public OtherProfilePhotosAdapter(Context context, List<String> photos) {
        this.context = context;
        this.photos = photos;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        String photoUrl = photos.get(position);
        Glide.with(context)
                .load(photoUrl)
                .placeholder(R.drawable.john)
                .into(holder.ivPhoto);
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }
    }
}
