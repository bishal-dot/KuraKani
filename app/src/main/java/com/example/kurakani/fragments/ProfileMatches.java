package com.example.kurakani.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.Adapter.MatchesAdapter;
import com.example.kurakani.R;
import com.example.kurakani.viewmodel.MatchesModel;
import com.example.kurakani.viewmodel.MatchViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileMatches extends Fragment {

    private static final String TAG = "ProfileMatches";

    private RecyclerView recyclerView;
    private MatchesAdapter adapter;
    private List<MatchesModel> matchList = new ArrayList<>();
    private TextView tvEmptyState;
    private ImageView ivEmptyIcon;
    private LinearLayout emptyStateContainer;
    private Spinner spinnerFilter;

    public ProfileMatches() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Inflating layout");
        return inflater.inflate(R.layout.fragment_profile_matches, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: Initializing views");

        recyclerView = view.findViewById(R.id.recyclerMatches);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        ivEmptyIcon = view.findViewById(R.id.ivEmptyIcon);
        emptyStateContainer = view.findViewById(R.id.emptyStateContainer);
        spinnerFilter = view.findViewById(R.id.spinnerFilter);

        adapter = new MatchesAdapter(requireContext(), matchList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Setup Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.spinner_item,
                getResources().getStringArray(R.array.match_filter_options)
        );
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        // Initialize ViewModel
        MatchViewModel matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);
        matchViewModel.initRepository(requireContext());
        Log.d(TAG, "ProfileMatches fragment loaded. Initial fetch will be triggered.");

        final int currentUserId = getLoggedInUserId();
        Log.d(TAG, "Logged-in user ID: " + currentUserId);

        // Observe LiveData
        matchViewModel.getMatchList().observe(getViewLifecycleOwner(), this::updateMatchList);
        matchViewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error received: " + error);
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        // Spinner listener
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "Spinner selected: " + selectedFilter);

                if (selectedFilter.equalsIgnoreCase("All")) {
                    matchViewModel.fetchMatches(currentUserId);
                } else {
                    matchViewModel.fetchMatchesByStatus(currentUserId, selectedFilter);
                }

                // Update empty text dynamically
                updateEmptyText(selectedFilter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Initial fetch
        matchViewModel.fetchMatches(currentUserId);
        updateEmptyText("All"); // Default empty state
    }

    /**
     * Updates the RecyclerView and shows/hides empty state container.
     */
    private void updateMatchList(List<MatchesModel> updatedMatches) {
        if (!isAdded()) return;

        List<MatchesModel> newList = (updatedMatches != null) ? new ArrayList<>(updatedMatches) : new ArrayList<>();
        matchList.clear();
        matchList.addAll(newList);
        adapter.notifyDataSetChanged();

        boolean isEmpty = newList.isEmpty();
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        emptyStateContainer.setVisibility(isEmpty ? View.VISIBLE : View.GONE);

        if (isEmpty) {
            String currentFilter = spinnerFilter.getSelectedItem() != null ?
                    spinnerFilter.getSelectedItem().toString() : "All";
            updateEmptyText(currentFilter);
        }
    }

    /**
     * Updates the empty state text and icon based on the currently selected filter.
     */
    private void updateEmptyText(String filter) {
        if (!isAdded()) return;

        switch (filter.toLowerCase()) {
            case "matched":
                tvEmptyState.setText("No matched users yet.");
                ivEmptyIcon.setImageResource(R.drawable.ic_no_matched);
                break;
            case "pending":
                tvEmptyState.setText("No pending requests.");
                ivEmptyIcon.setImageResource(R.drawable.ic_pending);
                break;
            case "all":
            default:
                tvEmptyState.setText("No matches available.");
                ivEmptyIcon.setImageResource(R.drawable.ic_no_matches);
                break;
        }
    }

    private int getLoggedInUserId() {
        if (getContext() == null) return -1;
        return getContext()
                .getSharedPreferences("KurakaniPrefs", Context.MODE_PRIVATE)
                .getInt("user_id", -1); // fallback if not found
    }
}
