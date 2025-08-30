package com.example.kurakani.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kurakani.Adapter.ChatUserAdapter;
import com.example.kurakani.R;
import com.example.kurakani.model.User;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.views.HomePageActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private RecyclerView rvChatList;
    private ChatUserAdapter adapter;
    private AutoCompleteTextView searchView;
    private LinearLayout tvEmptyState;
    private ArrayAdapter<User> searchAdapter;
    private Button btnStartChat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        rvChatList = view.findViewById(R.id.rvChatList);
        searchView = view.findViewById(R.id.searchView);
        tvEmptyState = view.findViewById(R.id.emptyStateLayout);
        btnStartChat = view.findViewById(R.id.btnStartChat);

        btnStartChat.setOnClickListener(v -> {
            searchView.requestFocus();
            searchView.setText("");
            showKeyboard();
        });

        adapter = new ChatUserAdapter(new ArrayList<>(), this::openIndividualChat);
        rvChatList.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatList.setAdapter(adapter);

        setupSearchAdapter();
        setupSearchView();
        fetchChatUsers();

        return view;
    }

    private void setupSearchAdapter() {
        searchAdapter = new ArrayAdapter<User>(getContext(), R.layout.item_user_dropdown, new ArrayList<>()) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.item_user_dropdown, parent, false);
                }

                User user = getItem(position);
                TextView tvUsername = view.findViewById(R.id.tvUsername);
                ImageView ivAvatar = view.findViewById(R.id.ivUserAvatar);

                if (user == null || user.getId() == 0) {
                    tvUsername.setText("No users found");
                    tvUsername.setTextColor(getResources().getColor(R.color.gray));
                    ivAvatar.setVisibility(View.GONE);
                } else {
                    tvUsername.setText(user.getFullname());
                    tvUsername.setTextColor(getResources().getColor(R.color.primary_color));
                    ivAvatar.setVisibility(View.VISIBLE);
                    if (user.getPhotos() != null && !user.getPhotos().isEmpty() &&
                            user.getPhotos().get(0).getUrl() != null) {
                        Glide.with(getContext())
                                .load(user.getPhotos().get(0).getUrl())
                                .placeholder(R.drawable.default_avatar)
                                .circleCrop()
                                .into(ivAvatar);
                    } else {
                        ivAvatar.setImageResource(R.drawable.default_avatar);
                    }
                }
                return view;
            }
        };

        searchView.setAdapter(searchAdapter);
        searchView.setThreshold(1);

        // Fix dropdown width/height and position
        searchView.setDropDownWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        searchView.setDropDownVerticalOffset(10);
        searchView.setDropDownAnchor(searchView.getId());
        searchView.setDropDownBackgroundResource(R.drawable.curved_rectangle);
    }

    private void setupSearchView() {
        searchView.setOnItemClickListener((parent, view, position, id) -> {
            User user = searchAdapter.getItem(position);
            if (user != null && user.getId() > 0) openIndividualChat(user);
            searchView.setText("");
            hideKeyboard();
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) searchUsersFromApi(query);
                else fetchChatUsers();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Focus + show keyboard
        searchView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                searchView.requestFocus();
                showKeyboard();
            }
            return false;
        });

        // Keyboard search action
        searchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchView.getText().toString().trim();
                if (!query.isEmpty()) searchUsersFromApi(query);
                return true;
            }
            return false;
        });

        // Clear drawable click
        searchView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (searchView.getRight() - searchView.getCompoundDrawables()[2].getBounds().width())) {
                    searchView.setText("");
                    return true;
                }
            }
            return false;
        });
    }

    private void searchUsersFromApi(String query) {
        ApiService apiService = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        Call<List<User>> call = apiService.searchUsers(query);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> users = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    users.addAll(response.body());
                } else {
                    User dummy = new User();
                    dummy.setId(0);
                    dummy.setFullname("No users found");
                    users.add(dummy);
                }

                searchAdapter.clear();
                searchAdapter.addAll(users);
                searchAdapter.notifyDataSetChanged();

                // Force proper dropdown display
                searchView.post(() -> {
                    searchView.dismissDropDown();
                    searchView.showDropDown();
                });

                adapter.updateList(users);
                updateEmptyState(users.isEmpty());
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Search failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchChatUsers() {
        ApiService apiService = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        Call<List<User>> call = apiService.getChatUsers();

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> users = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    users.addAll(response.body());
                }
                adapter.updateList(users);
                updateEmptyState(users.isEmpty());
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                adapter.updateList(new ArrayList<>());
                updateEmptyState(true);
            }
        });
    }

    private void openIndividualChat(User user) {
        if (user == null || user.getId() <= 0) return;

        Bundle bundle = new Bundle();
        bundle.putSerializable("chatUser", user);

        IndividualChat individualChatFragment = new IndividualChat();
        individualChatFragment.setArguments(bundle);

        if (getActivity() instanceof HomePageActivity) {
            ((HomePageActivity) getActivity()).switchFragment(individualChatFragment, true);
        }
    }

    private void updateEmptyState(boolean isEmpty) {
        if (tvEmptyState != null && rvChatList != null) {
            tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            rvChatList.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    private void showKeyboard() {
        if (getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideKeyboard() {
        if (getContext() != null) {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
    }
}
