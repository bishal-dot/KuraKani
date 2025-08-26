package com.example.kurakani.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.example.kurakani.R;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.HomePageActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = "New Notification";
        String body = "";
        String userId = null;

        // Notification payload
        if (remoteMessage.getNotification() != null) {
            title = remoteMessage.getNotification().getTitle();
            body = remoteMessage.getNotification().getBody();
        }

        // Data payload (contains matched user_id)
        if (remoteMessage.getData() != null && remoteMessage.getData().containsKey("user_id")) {
            userId = remoteMessage.getData().get("user_id");
        }

        showNotification(title, body, userId);
    }

    private void showNotification(String title, String message, String userId) {
        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "match_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Match Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        // ✅ Ensure HomePageActivity opens fresh and passes matched user_id
        Intent intent = new Intent(this, HomePageActivity.class);
        if (userId != null) {
            try {
                intent.putExtra("matched_user_id", Integer.parseInt(userId));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        // Use FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK to guarantee activity refresh
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                (int) System.currentTimeMillis(),
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // Handle token refresh
    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        // Send the new token to backend
        ApiService api = RetrofitClient.getInstance(getApplicationContext())
                .create(ApiService.class);

        HashMap<String, String> body = new HashMap<>();
        body.put("fcm_token", token);

        api.updateFcmToken(body).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Token successfully updated
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
