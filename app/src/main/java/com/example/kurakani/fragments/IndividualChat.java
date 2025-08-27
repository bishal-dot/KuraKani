package com.example.kurakani.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.Adapter.MessageAdapter;
import com.example.kurakani.model.Message;
import com.example.kurakani.model.User;
import com.example.kurakani.R;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndividualChat extends Fragment {

    private TextView tvChatWithUser;
    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend;

    private MessageAdapter adapter;
    private List<Message> messageList;

    private User chatUser;
    private int currentUserId = -1;

    private Handler handler = new Handler();
    private Runnable fetchMessagesRunnable;
    private static final int REFRESH_INTERVAL = 3000; // 3 seconds

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_chat, container, false);

        tvChatWithUser = view.findViewById(R.id.tvChatWithUser);
        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);

        if (getArguments() != null) {
            chatUser = (User) getArguments().getSerializable("chatUser");
            if (chatUser != null) {
                tvChatWithUser.setText(chatUser.getFullname());
            }
        }

        currentUserId = getLoggedInUserId();
        if (currentUserId == -1) {
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
            return view;
        }

        messageList = new ArrayList<>();
        adapter = new MessageAdapter(messageList, currentUserId);
        rvMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvMessages.setAdapter(adapter);

        btnSend.setOnClickListener(v -> sendMessage());
        startAutoRefresh();

        return view;
    }

    private int getLoggedInUserId() {
        if (getContext() == null) return -1;
        return getContext()
                .getSharedPreferences("KurakaniPrefs", Context.MODE_PRIVATE)
                .getInt("user_id", -1);
    }

    private void startAutoRefresh() {
        fetchMessagesRunnable = new Runnable() {
            @Override
            public void run() {
                fetchMessages();
                handler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        handler.post(fetchMessagesRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(fetchMessagesRunnable);
    }

    private void fetchMessages() {
        if (chatUser == null || currentUserId == -1) return;

        ApiService apiService = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        Call<List<Message>> call = apiService.getMessages(chatUser.getId());

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateMessages(response.body()); // Merge without clearing
                    rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage() {
        if (chatUser == null || currentUserId == -1) return;

        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        ApiService apiService = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        ApiService.SendMessageRequest request = new ApiService.SendMessageRequest(chatUser.getId(), msg);

        Call<Message> call = apiService.sendMessage(request);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.addMessage(response.body());
                    rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                    etMessage.setText("");
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
