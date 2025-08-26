package com.example.kurakani.viewmodel;

import androidx.annotation.NonNull;
import android.content.Context;
import android.util.Log;

import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MatchRepository {

    private static final String TAG = "MatchRepository";

    private final ApiService matchApi;

    public MatchRepository(@NonNull Context context) {
        matchApi = RetrofitClient.getClient(context).create(ApiService.class);
    }

    public interface MatchCallback {
        void onMatchesFetched(List<MatchesModel> matches);
        void onError(Throwable t);
    }

    /**
     * Fetch matches initiated by a specific user.
     *
     * @param userId  ID of the logged-in user
     * @param status  null for "All", or "Matched"/"Pending" for filtering
     * @param callback Callback interface for results
     */
    public void getMatches(int userId, String status, final MatchCallback callback) {
        Log.d(TAG, "Fetching matches for user_id=" + userId + ", status=" + status);

        matchApi.getMatches(userId, status)
                .enqueue(new Callback<List<MatchesModel>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<MatchesModel>> call,
                                           @NonNull Response<List<MatchesModel>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            List<MatchesModel> matches = response.body();

                            // DEBUG: log all matches received
                            Log.d(TAG, "Matches fetched: " + matches.size());
                            for (MatchesModel match : matches) {
                                Log.d(TAG, "Match -> ID: " + match.getId() +
                                        ", Name: " + match.getName() +
                                        ", Status: " + match.getStatus());
                            }

                            callback.onMatchesFetched(matches);

                        } else {
                            Log.d(TAG, "Response empty or unsuccessful: " + response.code());
                            callback.onError(new Exception("No matches found or response error"));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<MatchesModel>> call, @NonNull Throwable t) {
                        Log.e(TAG, "API call failed", t);
                        callback.onError(t);
                    }
                });
    }
}
