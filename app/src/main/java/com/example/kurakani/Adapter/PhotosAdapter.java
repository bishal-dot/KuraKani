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
    private final List<ProfileResponse.User.UserPhoto> photos; // <-- fixed type
    private final PhotoClickListener listener;

    public interface PhotoClickListener {
        void onPhotoDeleteClick(int position, ProfileResponse.User.UserPhoto photo);
    }

    public PhotosAdapter(Context context, List<ProfileResponse.User.UserPhoto> photos, PhotoClickListener listener) {
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
        ProfileResponse.User.UserPhoto photo = photos.get(position);

        Glide.with(holder.itemView.getContext())
                .load(photo.url != null ? photo.url : R.drawable.profile_icon)
                .placeholder(R.drawable.profile_icon)
                .error(R.drawable.profile_icon)
                .into(holder.ivPhoto);

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPhotoDeleteClick(position, photo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return photos.size();
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto, btnDelete;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            btnDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}
