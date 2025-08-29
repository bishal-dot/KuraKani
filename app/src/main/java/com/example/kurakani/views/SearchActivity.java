package com.example.kurakani.views;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.Adapter.SearchProfileAdapter;
import com.example.kurakani.R;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.model.SearchResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView resultsRecyclerView;
    private ProgressBar progressBar;
    private View emptyState;
    private TextInputEditText searchEditText;
    private ChipGroup chipGroup;

    private SearchProfileAdapter adapter;
    private List<ProfileResponse.User> profileList = new ArrayList<>();
    private List<String> selectedInterests = new ArrayList<>();
    private List<String> allInterests = new ArrayList<>();
    private int currentUserId;

    private ApiService apiService;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private static final long SEARCH_DELAY = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SharedPreferences prefs = getSharedPreferences("KurakaniPrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        toolbar = findViewById(R.id.toolbar);
        searchEditText = findViewById(R.id.searchEditText);
        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        chipGroup = findViewById(R.id.filterChipGroup);

        toolbar.setNavigationOnClickListener(v -> finish());

        adapter = new SearchProfileAdapter(this, profileList);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.setAdapter(adapter);

        apiService = RetrofitClient.getInstance(this).create(ApiService.class);

        fetchInterests();
        setupSearchListeners();
    }

    private void setupSearchListeners() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> fetchUsers(searchEditText.getText().toString(), selectedInterests);
                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY);
            }
        });

        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                fetchUsers(searchEditText.getText().toString(), selectedInterests);
                return true;
            }
            return false;
        });
    }

    private void fetchInterests() {
        apiService.getInterests().enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allInterests = response.body();
                    setupChips();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Failed to load interests", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupChips() {
        chipGroup.removeAllViews();
        for (String interest : allInterests) {
            Chip chip = new Chip(this);
            chip.setText(interest);
            chip.setCheckable(true);
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String interestLower = interest.toLowerCase().trim();
                if (isChecked) selectedInterests.add(interestLower);
                else selectedInterests.remove(interestLower);

                fetchUsers(searchEditText.getText().toString(), selectedInterests);
            });
            chipGroup.addView(chip);
        }
    }

    private void fetchUsers(String query, List<String> interests) {
        profileList.clear();
        adapter.notifyDataSetChanged();

        if (TextUtils.isEmpty(query) && (interests == null || interests.isEmpty())) {
            emptyState.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        StringBuilder searchBuilder = new StringBuilder();
        if (!TextUtils.isEmpty(query)) searchBuilder.append(query);
        if (interests != null && !interests.isEmpty()) {
            for (String interest : interests) searchBuilder.append(" ").append(interest);
        }
        String searchParam = searchBuilder.toString().toLowerCase();

        apiService.searchUsers(searchParam).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getUsers() != null) {
                    profileList.addAll(response.body().getUsers());
                    emptyState.setVisibility(profileList.isEmpty() ? View.VISIBLE : View.GONE);
                    adapter.notifyDataSetChanged();
                } else {
                    emptyState.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
