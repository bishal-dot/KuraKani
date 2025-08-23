package com.example.kurakani.fragments;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    private ImageView btnMatch, btnReject;

    private static final int DAILY_SWIPE_LIMIT = 5;
    private int dailySwipeCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recyclerViewProfiles);
        tvWelcome = view.findViewById(R.id.tvWelcome);
        tvNoProfiles = view.findViewById(R.id.tvNoProfiles);
        premiumOverlay = view.findViewById(R.id.premiumOverlay);
        btnRefreshProfiles = view.findViewById(R.id.btnRefreshProfiles);
        btnMatch = view.findViewById(R.id.btnMatch);
        btnReject = view.findViewById(R.id.btnReject);
        tvSearch = view.findViewById(R.id.tvSearch);

        tvSearch.setOnClickListener(v -> {
            // Open SearchActivity
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        fetchLoggedInUser();

        adapter = new ProfileAdapter(profileList, new ProfileAdapter.OnSwipeListener() {
            @Override
            public void onMatch(ProfileModel profile) {
                dailySwipeCount++;
                Toast.makeText(getContext(), "Matched with " + profile.getFullname(), Toast.LENGTH_SHORT).show();
                checkDailyLimit();
            }

            @Override
            public void onReject(ProfileModel profile) {
                dailySwipeCount++;
                Toast.makeText(getContext(), "Rejected " + profile.getFullname(), Toast.LENGTH_SHORT).show();
                checkDailyLimit();
            }
        });

        adapter.setOnItemClickListener(profile -> fetchFullProfileAndOpen(profile.getId()));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return false; // prevent scroll
            }
        });
        recyclerView.setAdapter(adapter);

        setupSwipeGestures();
        setupButtons();

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
                if (viewHolder.getAdapterPosition() != 0) {
                    adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    return; // only top card swipeable
                }

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


    private void setupButtons() {
        btnMatch.setOnClickListener(v -> {
            if (!profileList.isEmpty()) {
                ProfileModel profile = profileList.get(0);
                adapter.swipeListener.onMatch(profile);
                removeTopProfile();
            }
        });

        btnReject.setOnClickListener(v -> {
            if (!profileList.isEmpty()) {
                ProfileModel profile = profileList.get(0);
                adapter.swipeListener.onReject(profile);
                removeTopProfile();
            }
        });
    }

    private void removeTopProfile() {
        if (!profileList.isEmpty()) {
            profileList.remove(0);
            adapter.notifyDataSetChanged();

            recyclerView.post(() -> {
                for (int i = 0; i < recyclerView.getChildCount(); i++) {
                    View child = recyclerView.getChildAt(i);
                    float scale = 1 - 0.05f * i;
                    float translationY = 20f * i;
                    child.animate().scaleX(scale).scaleY(scale).translationY(translationY).alpha(1f).setDuration(200).start();
                }
            });

            if (profileList.isEmpty() && dailySwipeCount < DAILY_SWIPE_LIMIT) showNoProfilesMessage();
        }
    }

    private void fetchProfiles() {
        ApiService api = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        api.otherUsers().enqueue(new Callback<List<ProfileResponse.User>>() {
            @Override
            public void onResponse(Call<List<ProfileResponse.User>> call, Response<List<ProfileResponse.User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    profileList.clear();
                    for (ProfileResponse.User user : response.body()) {
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
                                user.profile,
                                interestsList
                        ));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<ProfileResponse.User>> call, Throwable t) {
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
                    ProfileModel fullProfile = convertApiUserToProfile(response.body());
                    ProfileExpanded fragment = ProfileExpanded.newInstance(fullProfile);
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .addToBackStack(null)
                            .commit();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse.User> call, Throwable t) {
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ProfileModel convertApiUserToProfile(ProfileResponse.User apiUser) {
        int age = apiUser.age != null ? apiUser.age : 0;
        String fullname = apiUser.fullname != null ? apiUser.fullname : "";
        String username = apiUser.username != null ? apiUser.username : "";
        String gender = apiUser.gender != null ? apiUser.gender : "";
        String purpose = apiUser.purpose != null ? apiUser.purpose : "";
        String about = apiUser.about != null ? apiUser.about : "";

        String profile = "";
        if (apiUser.profile != null && !apiUser.profile.isEmpty()) {
            profile = apiUser.profile.startsWith("http") ? apiUser.profile : RetrofitClient.BASE_URL + "storage/" + apiUser.profile;
        }

        List<String> interests = apiUser.interests != null ? apiUser.interests : new ArrayList<>();
        List<String> photos = new ArrayList<>();
        if (apiUser.photos != null) {
            for (ProfileResponse.User.Photo photo : apiUser.photos) {
                if (photo != null && photo.url != null && !photo.url.isEmpty()) {
                    String fullUrl = photo.url.startsWith("http") ? photo.url : RetrofitClient.BASE_URL + "storage/" + photo.url;
                    photos.add(fullUrl);
                }
            }
        }

        return new ProfileModel(apiUser.id, fullname, username, age, gender, purpose, about, profile, interests, photos);
    }

    private void fetchLoggedInUser() {
        ApiService api = RetrofitClient.getInstance(getContext()).create(ApiService.class);
        api.getProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().user != null) {
                    String fullname = response.body().user.fullname;
                    tvWelcome.setText("Welcome, " + (fullname != null ? fullname : response.body().user.username));
                } else {
                    tvWelcome.setText("Welcome, User");
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                tvWelcome.setText("Welcome, User");
            }
        });
    }

    private void checkDailyLimit() {
        if (dailySwipeCount >= DAILY_SWIPE_LIMIT) showPremiumOverlay();
    }

    private void showNoProfilesMessage() {
        tvNoProfiles.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        hidePremiumOverlay();
    }

    private void showPremiumOverlay() {
        premiumOverlay.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        tvNoProfiles.setVisibility(View.GONE);
    }

    private void hidePremiumOverlay() {
        premiumOverlay.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }
}
