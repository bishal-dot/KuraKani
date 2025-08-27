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
import com.example.kurakani.model.ProfileResponse;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder> {

    private final Context context;
    private final List<ProfileResponse.UserPhoto> photos;
    private final PhotoClickListener listener;

    public interface PhotoClickListener {
        void onPhotoDeleteClick(int position, ProfileResponse.UserPhoto photo);
    }

    public PhotosAdapter(Context context, List<ProfileResponse.UserPhoto> photos, PhotoClickListener listener) {
        this.context = context;
        this.photos = photos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        ProfileResponse.UserPhoto photo = photos.get(position);

        Glide.with(context)
                .load(photo.getUrl())
                .placeholder(R.drawable.profile_icon)
                .error(R.drawable.profile_icon)
                .into(holder.ivPhoto);

        holder.btnDelete.setOnClickListener(v -> listener.onPhotoDeleteClick(position, photo));
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        ImageView btnDelete;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            btnDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
