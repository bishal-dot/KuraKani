package com.example.kurakani.Adapter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kurakani.R;
import com.example.kurakani.model.ProfileModel;
import java.util.List;
import java.util.Random;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private final List<ProfileModel> profileList;
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

        holder.btnMatch.setOnClickListener(v -> {
            sendMatchNotification(user);
            if (matchListener != null) matchListener.onUserMatched(user);
        });

        holder.btnReject.setOnClickListener(v -> {
            // Optional: handle rejection
        });
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }

    static class ProfileViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView nameAge, bio, hobbies;
        ImageButton btnMatch, btnReject;

        public ProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.profileImageView);
            nameAge = itemView.findViewById(R.id.tv_nameAge);
            bio = itemView.findViewById(R.id.tv_bio);
            hobbies = itemView.findViewById(R.id.tv_hobbies);
            btnMatch = itemView.findViewById(R.id.btnMatch);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }

    private void sendMatchNotification(ProfileModel user) {
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
}