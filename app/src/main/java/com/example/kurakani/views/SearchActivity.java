package com.example.kurakani.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.Adapter.SearchProfileAdapter;
import com.example.kurakani.R;
import com.example.kurakani.model.SearchResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.viewmodel.ProfileModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView resultsRecyclerView;
    private ProgressBar progressBar;
    private View emptyState;
    private TextInputEditText searchEditText;
    private ChipGroup filterChipGroup;

    private SearchProfileAdapter adapter;
    private List<ProfileModel> profileList = new ArrayList<>();
    private List<String> selectedInterests = new ArrayList<>();

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        resultsRecyclerView = findViewById(R.id.resultsRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        searchEditText = findViewById(R.id.searchEditText);
        filterChipGroup = findViewById(R.id.filterChipGroup);

        apiService = RetrofitClient.getInstance(this).create(ApiService.class);

        adapter = new SearchProfileAdapter(profileList);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        resultsRecyclerView.setAdapter(adapter);

        // Load available interests from backend
//        fetchInterests();

        // Search text listener
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                fetchUsers(s.toString(), selectedInterests);
            }
        });
    }

//    private void fetchInterests() {
//        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
//
//        apiService.getInterests().enqueue(new Callback<List<String>>() {
//            @Override
//            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    filterChipGroup.removeAllViews(); // clear previous chips
//                    for (String interest : response.body()) {
//                        Chip chip = new Chip(SearchActivity.this);
//                        chip.setText(interest);
//                        chip.setCheckable(true);
//                        chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                            if (isChecked) selectedInterests.add(interest);
//                            else selectedInterests.remove(interest);
//
//                            // Convert selected interests to comma-separated string
//                            String interestParam = TextUtils.join(",", selectedInterests);
//                            fetchUsers(searchEditText.getText().toString(), interestParam);
//                        });
//                        filterChipGroup.addView(chip);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<String>> call, Throwable t) {
//                Toast.makeText(SearchActivity.this, "Failed to load interests", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
    private void fetchUsers(String searchQuery, List<String> interests) {
        progressBar.setVisibility(View.VISIBLE);

        String interestParam = String.join(",", interests);

        apiService.searchUsers(searchQuery, interestParam).enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                progressBar.setVisibility(View.GONE);
                profileList.clear();

                if (response.isSuccessful() && response.body() != null
                        && response.body().getUsers() != null
                        && !response.body().getUsers().isEmpty()) {

                    profileList.addAll(response.body().getUsers());
                    emptyState.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
