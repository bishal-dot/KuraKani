package com.example.kurakani.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

    private static final int PICK_IMAGE_REQUEST = 101;

    private TextView tvChatWithUser;
    private RecyclerView rvMessages;
    private EditText etMessage;
    private ImageButton btnSend, btnPickImage;
    private View rootLayout;
    private ImageView ivProfile;

    private MessageAdapter adapter;
    private List<Message> messageList;

    private User chatUser;
    private int currentUserId = -1;

    private android.os.Handler handler = new android.os.Handler();
    private Runnable fetchMessagesRunnable;
    private static final int REFRESH_INTERVAL = 3000; // 3 seconds

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_individual_chat, container, false);

        rootLayout = view.findViewById(R.id.rootLayout);
        tvChatWithUser = view.findViewById(R.id.tvChatWithUser);
        rvMessages = view.findViewById(R.id.rvMessages);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnPickImage = view.findViewById(R.id.btnPickImage);
        ivProfile = view.findViewById(R.id.ivChatUserProfile);

        if (getArguments() != null) {
            chatUser = (User) getArguments().getSerializable("chatUser");
            if (chatUser != null) {
                tvChatWithUser.setText(chatUser.getFullname());
                Glide.with(this)
                        .load(chatUser.getProfile())
                        .placeholder(R.drawable.default_avatar)
                        .circleCrop()
                        .into(ivProfile);
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
        btnPickImage.setOnClickListener(v -> pickImageFromGallery());

        startAutoRefresh();
        setupKeyboardInsetsListener();

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
                    boolean isAtBottom = !rvMessages.canScrollVertically(1);
                    adapter.updateMessages(response.body());

                    if (isAtBottom) {
                        rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                    }
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

    // --- Image Picking / Sending ---
    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            uploadImageAndSendMessage(selectedImageUri);
        }
    }

    private void uploadImageAndSendMessage(Uri imageUri) {
        if (imageUri == null || chatUser == null) return;

        // Upload to server & get URL
        String uploadedImageUrl = uploadImageToServer(imageUri);

        if (uploadedImageUrl == null) {
            Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send image message
        ApiService apiService = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        ApiService.SendMessageRequest request = new ApiService.SendMessageRequest(chatUser.getId(), uploadedImageUrl, true);

        Call<Message> call = apiService.sendMessage(request);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.addMessage(response.body());
                    rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to send image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String uploadImageToServer(Uri imageUri) {
        // Implement your server upload logic here and return the image URL
        // For now, return a placeholder for testing
        return "https://example.com/uploaded_image.png";
    }
    // --- End of Image Sending ---

    private void setupKeyboardInsetsListener() {
        View inputLayout = rootLayout.findViewById(R.id.inputLayout);

        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            int imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;

            inputLayout.setPadding(
                    inputLayout.getPaddingLeft(),
                    inputLayout.getPaddingTop(),
                    inputLayout.getPaddingRight(),
                    imeHeight
            );

            rvMessages.setPadding(
                    rvMessages.getPaddingLeft(),
                    rvMessages.getPaddingTop(),
                    rvMessages.getPaddingRight(),
                    imeHeight
            );

            rvMessages.post(() -> rvMessages.scrollToPosition(adapter.getItemCount() - 1));

            return insets;
        });
    }
}
