package com.example.kurakani.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MatchViewModel extends ViewModel {

    private static final String TAG = "MatchViewModel";

    private final MutableLiveData<List<MatchesModel>> matchList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MatchRepository repository;

    // Initialize repository with context
    public void initRepository(Context context) {
        if (repository == null) {
            repository = new MatchRepository(context);
            Log.d(TAG, "Repository initialized");
        } else {
            Log.d(TAG, "Repository already initialized");
        }
    }

    // LiveData observed by the Fragment
    public LiveData<List<MatchesModel>> getMatchList() {
        return matchList;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Fetch all matches for a specific user (status = null means "All")
     *
     * @param userId Logged-in user's ID
     */
    public void fetchMatches(int userId) {
        if (repository == null) {
            Log.e(TAG, "Repository is null! Cannot fetch matches.");
            return;
        }
        Log.d(TAG, "Fetching all matches for userId=" + userId);

        repository.getMatches(userId, null, new MatchRepository.MatchCallback() {
            @Override
            public void onMatchesFetched(List<MatchesModel> matches) {
                Log.d(TAG, "Matches fetched in ViewModel: " + (matches != null ? matches.size() : "null"));
                matchList.setValue(matches);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Error fetching matches in ViewModel", t);
                errorMessage.setValue(t.getMessage());
            }
        });
    }

    /**
     * Fetch matches filtered by status for a specific user
     *
     * @param userId Logged-in user's ID
     * @param status "Matched" or "Pending"
     */
    public void fetchMatchesByStatus(int userId, String status) {
        if (repository == null) {
            Log.e(TAG, "Repository is null! Cannot fetch matches by status.");
            return;
        }
        Log.d(TAG, "Fetching matches for userId=" + userId + ", status=" + status);

        repository.getMatches(userId, status, new MatchRepository.MatchCallback() {
            @Override
            public void onMatchesFetched(List<MatchesModel> matches) {
                Log.d(TAG, "Matches fetched in ViewModel (status " + status + "): " +
                        (matches != null ? matches.size() : "null"));
                matchList.setValue(matches);
            }

            @Override
            public void onError(Throwable t) {
                Log.e(TAG, "Error fetching matches by status in ViewModel", t);
                errorMessage.setValue(t.getMessage());
            }
        });
    }
}
