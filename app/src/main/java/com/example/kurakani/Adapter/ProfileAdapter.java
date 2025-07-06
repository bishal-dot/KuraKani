package com.example.kurakani.Adapter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.R;
import com.example.kurakani.viewmodel.ProfileModel;

import java.util.List;
import java.util.Random;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<ProfileModel> profileList;
    private final Context context;
    private final OnMatchClickListener matchListener;

    public ProfileAdapter(Context context, List<ProfileModel> profileList, OnMatchClickListener matchListener) {
        this.context = context;
        this.profileList = profileList;
        this.matchListener = matchListener;
        createMatchChannel();
    }

    @NonNull
    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_profile_card, parent, false);
        return new ProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        ProfileModel user = profileList.get(position);

        holder.nameAge.setText(user.getName() + ", " + user.getAge());
        holder.bio.setText(user.getBio());
        holder.hobbies.setText(user.getHobbies());
        holder.image.setImageResource(user.getImageResId());

        // Reset animations when view is recycled
        holder.lottieMatch.setVisibility(View.GONE);
        holder.lottieMatch.setProgress(0f);
        holder.lottieMatch.cancelAnimation();

        holder.lottieReject.setVisibility(View.GONE);
        holder.lottieReject.setProgress(0f);
        holder.lottieReject.cancelAnimation();
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    public void removeAt(int position) {
        if (position >= 0 && position < profileList.size()) {
            profileList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void playMatchAnimation(ProfileViewHolder holder) {
        holder.lottieMatch.post(() -> {
            holder.lottieMatch.setVisibility(View.VISIBLE);
            holder.lottieMatch.setAlpha(1f);
            holder.lottieMatch.playAnimation();
        });
    }

    public void playRejectAnimation(ProfileViewHolder holder) {
        holder.lottieReject.post(() -> {
            holder.lottieReject.setVisibility(View.VISIBLE);
            holder.lottieReject.setAlpha(1f);
            holder.lottieReject.playAnimation();
        });
    }

    public void sendMatchNotification(ProfileModel user) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "matchChannel")
                .setSmallIcon(R.drawable.matched)
                .setContentTitle("It's a Match!")
                .setContentText("You matched with " + user.getName())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(new Random().nextInt(), builder.build());
    }

    private void createMatchChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "matchChannel",
                    "Match Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kurakani match alerts");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public interface OnMatchClickListener {
        void onUserMatched(ProfileModel user);
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nameAge, bio, hobbies;
        public LottieAnimationView lottieMatch, lottieReject;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profileImageView);
            nameAge = itemView.findViewById(R.id.tv_nameAge);
            bio = itemView.findViewById(R.id.tv_bio);
            hobbies = itemView.findViewById(R.id.tv_hobbies);
            lottieMatch = itemView.findViewById(R.id.lottieMatch);
            lottieReject = itemView.findViewById(R.id.lottieReject);
        }
    }

    public void updateList(List<ProfileModel> newList) {
        this.profileList = newList;
        notifyDataSetChanged();
    }
}