package com.example.kurakani.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.Adapter.ProfileAdapter;
import com.example.kurakani.R;
import com.example.kurakani.model.ProfileResponse;
import com.example.kurakani.network.ApiService;
import com.example.kurakani.network.RetrofitClient;
import com.example.kurakani.viewmodel.ProfileModel;
import com.example.kurakani.views.SearchActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePageFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProfileAdapter adapter;
    private List<ProfileModel> profileList = new ArrayList<>();
    private TextView tvWelcome, tvNoProfiles, tvSearch;
    private View premiumOverlay;
    private Button btnRefreshProfiles;
    private View unverifiedOverlay;
    private Button btnVerifyAccount;

    private boolean isLoggedInUserVerified = false;
    private static final int DAILY_SWIPE_LIMIT = 5;
    private int dailySwipeCount = 0;

    private static final String ARG_USER = "arg_user";
    private ProfileResponse.User loggedInUser;

    public static HomePageFragment newInstance(ProfileResponse.User user) {
        HomePageFragment fragment = new HomePageFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            loggedInUser = (ProfileResponse.User) getArguments().getSerializable(ARG_USER);
            Log.d("HomePageFragment", "Logged in user from args: " + (loggedInUser != null ? loggedInUser.fullname : "null"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        unverifiedOverlay = view.findViewById(R.id.unverifiedOverlay);
        btnVerifyAccount = view.findViewById(R.id.btnVerifyAccount);
        recyclerView = view.findViewById(R.id.recyclerViewProfiles);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvNoProfiles = view.findViewById(R.id.tvNoProfiles);
        premiumOverlay = view.findViewById(R.id.premiumOverlay);
        btnRefreshProfiles = view.findViewById(R.id.btnRefreshProfiles);
        tvSearch = view.findViewById(R.id.tvSearch);

        btnVerifyAccount.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new ProfilePictureVerification())
                    .addToBackStack(null)
                    .commit();
        });

        tvSearch.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchActivity.class)));

        fetchLoggedInUser();

        adapter = new ProfileAdapter(profileList, new ProfileAdapter.OnSwipeListener() {
            @Override
            public void onMatch(ProfileModel profile) {
                dailySwipeCount++;
                Log.d("SwipeDebug", "Matched: " + profile.getFullname());
                checkDailyLimit();
            }

            @Override
            public void onReject(ProfileModel profile) {
                dailySwipeCount++;
                Log.d("SwipeDebug", "Rejected: " + profile.getFullname());
                checkDailyLimit();
            }
        });

        adapter.setOnItemClickListener(profile -> {
            if (!isLoggedInUserVerified) {
                Toast.makeText(getContext(), "Verify your account to view profiles", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchFullProfileAndOpen(profile.getId());
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() { return false; }
        });
        recyclerView.setAdapter(adapter);

        setupSwipeGestures();

        btnRefreshProfiles.setOnClickListener(v -> {
            tvNoProfiles.setVisibility(View.GONE);
            hidePremiumOverlay();
            fetchProfiles();
        });

        fetchProfiles();
    }

    private void setupSwipeGestures() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) { return false; }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (profileList.isEmpty()) return;

                ProfileModel profile = profileList.get(0);
                if (direction == ItemTouchHelper.RIGHT) adapter.swipeListener.onMatch(profile);
                else adapter.swipeListener.onReject(profile);

                adapter.removeTopItem();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (viewHolder.getAdapterPosition() == 0) {
                    float width = viewHolder.itemView.getWidth();
                    float alpha = 1 - Math.min(Math.abs(dX) / width, 1f);
                    viewHolder.itemView.setAlpha(alpha);
                    viewHolder.itemView.setTranslationX(dX);
                    viewHolder.itemView.setTranslationY(dY);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }

    private void fetchProfiles() {
        ApiService api = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        api.otherUsers().enqueue(new Callback<List<ProfileResponse.User>>() {
            @Override
            public void onResponse(Call<List<ProfileResponse.User>> call, Response<List<ProfileResponse.User>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.e("ProfileFetch", "Failed response");
                    return;
                }

                profileList.clear();
                List<ProfileResponse.User> users = response.body();

                for (ProfileResponse.User user : users) {
                    List<String> interestsList = new ArrayList<>();
                    if (user.interests != null) {
                        try {
                            interestsList = new Gson().fromJson(user.interests.toString(),
                                    new TypeToken<List<String>>(){}.getType());
                        } catch (Exception e) {
                            interestsList = Arrays.asList(user.interests.toString().split(","));
                        }
                    }

                    profileList.add(new ProfileModel(
                            user.id,
                            user.fullname != null ? user.fullname : user.username,
                            user.username,
                            user.age != null ? user.age : 0,
                            user.profile != null ? user.profile : "",
                            interestsList,
                            user.is_verified != null ? user.is_verified : false
                    ));
                }

                adapter.notifyDataSetChanged();
                if (profileList.isEmpty()) {
                    tvNoProfiles.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoProfiles.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<ProfileResponse.User>> call, Throwable t) {
                Log.e("ProfileFetch", "Error fetching profiles", t);
                Toast.makeText(getContext(), "Failed to load profiles", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchFullProfileAndOpen(int userId) {
        ApiService api = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        api.getUserProfile(userId).enqueue(new Callback<ProfileResponse.User>() {
            @Override
            public void onResponse(Call<ProfileResponse.User> call, Response<ProfileResponse.User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ProfileModel profile = convertApiUserToProfile(response.body());
                    com.example.kurakani.fragments.ProfileExpanded fragment = com.example.kurakani.fragments.ProfileExpanded.newInstance(profile);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse.User> call, Throwable t) {
                Log.e("ProfileFetch", "Error fetching full profile", t);
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ProfileModel convertApiUserToProfile(ProfileResponse.User apiUser) {
        int age = apiUser.age != null ? apiUser.age : 0;
        String fullname = apiUser.fullname != null ? apiUser.fullname : "";
        String username = apiUser.username != null ? apiUser.username : "";
        String profile = apiUser.profile != null ? apiUser.profile : "";
        List<String> interests = apiUser.interests != null ? apiUser.interests : new ArrayList<>();
        boolean isVerified = apiUser.is_verified != null && apiUser.is_verified;

        return new ProfileModel(apiUser.id, fullname, username, age, profile, interests, isVerified);
    }

    private void fetchLoggedInUser() {
        ApiService api = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        api.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUser() != null) {
                    ProfileResponse.User user = response.body().getUser();
                    isLoggedInUserVerified = user.is_verified != null && user.is_verified;
                    tvWelcome.setText("Welcome, " + (user.fullname != null ? user.fullname : user.username));
                    unverifiedOverlay.setVisibility(isLoggedInUserVerified ? View.GONE : View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.e("HomePageFragment", "Error fetching logged in user", t);
                tvWelcome.setText("Welcome, User");
            }
        });
    }

    private void checkDailyLimit() {
        if (dailySwipeCount >= DAILY_SWIPE_LIMIT) premiumOverlay.setVisibility(View.VISIBLE);
    }

    private void hidePremiumOverlay() {
        premiumOverlay.setVisibility(View.GONE);
    }
}
