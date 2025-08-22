package com.example.kurakani.fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;
import com.example.kurakani.Adapter.MessageAdapter;
import com.example.kurakani.R;
import com.example.kurakani.model.Message;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IndividualChat extends Fragment {

    private static final String ARG_OTHER_ID = "otherUserId";
    private static final String ARG_OTHER_NAME = "otherName";

    public static IndividualChat newInstance(int otherUserId, String otherName) {
        IndividualChat f = new IndividualChat();
        Bundle b = new Bundle();
        b.putInt(ARG_OTHER_ID, otherUserId);
        b.putString(ARG_OTHER_NAME, otherName);
        f.setArguments(b);
        return f;
    }

    private int otherUserId = 0;
    private String otherName = "User";
    ImageButton backButton;
    private RecyclerView recycler;
    private EditText etMessage;
    private Button btnSend;
    private TextView tvHeader;

    private MessageAdapter adapter;
    private final List<Message> messages = new ArrayList<>();

    private ApiService api;
    private final Handler pollHandler = new Handler(Looper.getMainLooper());
    private final int POLL_MS = 3000;

    private final Runnable pollRunnable = new Runnable() {
        @Override public void run() {
            fetchMessages(false);
            pollHandler.postDelayed(this, POLL_MS);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_individual_chat, container, false);

        recycler = v.findViewById(R.id.recyclerViewMessages);
        etMessage = v.findViewById(R.id.etMessage);
        btnSend   = v.findViewById(R.id.btnSend);
        tvHeader  = v.findViewById(R.id.tvHeader);
        backButton = v.findViewById(R.id.backButton);

        // Back button
        backButton.setOnClickListener(view -> {
            if (isAdded()) getActivity().onBackPressed();
        });

        if (getArguments() != null) {
            otherUserId = getArguments().getInt(ARG_OTHER_ID, 0);
            otherName   = getArguments().getString(ARG_OTHER_NAME, "User");
        }
        tvHeader.setText("Chat with " + otherName + " (#" + otherUserId + ")");

        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new MessageAdapter(messages, RetrofitClient.CURRENT_USER_ID);
        recycler.setAdapter(adapter);

        api = RetrofitClient.getClientWithUserId(requireContext()).create(ApiService.class);

        btnSend.setOnClickListener(view -> {
            String text = etMessage.getText().toString().trim();
            if (text.isEmpty()) return;
            sendMessage(text);
        });

        // Initial load
        fetchMessages(true);

        return v;
    }

    private void fetchMessages(boolean scrollToEnd) {
        api.getMessages(otherUserId).enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> resp) {
                if (!isAdded()) return;

                if (!resp.isSuccessful()) {
                    try {
                        String errorBody = resp.errorBody() != null ? resp.errorBody().string() : "null";
                        Log.e("IndividualChat", "Fetch failed: HTTP " + resp.code() + " Body: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(requireContext(), "Load failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (resp.body() == null) {
                    Toast.makeText(requireContext(), "Empty response", Toast.LENGTH_SHORT).show();
                    return;
                }

                adapter.replaceAll(resp.body());
                recycler.scrollToPosition(adapter.getItemCount() - 1);
            }


            @Override public void onFailure(Call<List<Message>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String text) {
        api.sendMessage(otherUserId, text).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> resp) {
                if (!isAdded()) return;

                if (!resp.isSuccessful() || resp.body() == null) {
                    try {
                        String errorBody = resp.errorBody() != null ? resp.errorBody().string() : "null";
                        Log.e("IndividualChat", "Send failed: HTTP " + resp.code() + " Body: " + errorBody);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(requireContext(), "Send failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                adapter.addOne(resp.body());
                recycler.scrollToPosition(adapter.getItemCount() - 1);
                etMessage.setText("");
            }


            @Override public void onFailure(Call<Message> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override public void onResume() {
        super.onResume();
        pollHandler.postDelayed(pollRunnable, POLL_MS);
    }

    @Override public void onPause() {
        super.onPause();
        pollHandler.removeCallbacks(pollRunnable);
    }
}