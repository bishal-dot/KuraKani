package com.example.kurakani.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.Adapter.ProfileAdapter;
import com.example.kurakani.Adapter.StoryAdapter;
import com.example.kurakani.R;
import com.example.kurakani.viewmodel.MatchModel;
import com.example.kurakani.viewmodel.MatchViewModel;
import com.example.kurakani.viewmodel.ProfileModel;
import com.example.kurakani.viewmodel.StoryModel;

import java.util.ArrayList;
import java.util.List;

public class HomePageFragment extends Fragment {

    private RecyclerView recyclerViewStory, recyclerViewProfiles;
    private ProfileAdapter adapter;
    private List<ProfileModel> fullProfileList = new ArrayList<>();
    private List<ProfileModel> filteredProfileList = new ArrayList<>();

    public HomePageFragment() {
        // Required empty public constructor
    }

    private static class NoScrollLinearLayoutManager extends LinearLayoutManager {
        public NoScrollLinearLayoutManager(@NonNull Context context) {
            super(context);
        }

        @Override
        public boolean canScrollVertically() {
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MatchViewModel matchViewModel = new ViewModelProvider(requireActivity()).get(MatchViewModel.class);

        recyclerViewStory = view.findViewById(R.id.recyclerViewStory);
        recyclerViewProfiles = view.findViewById(R.id.recyclerViewProfiles);

        // Setup Story List
        List<StoryModel> storyList = new ArrayList<>();
        storyList.add(new StoryModel(R.drawable.john, "Bishal"));
        storyList.add(new StoryModel(R.drawable.kori, "Bishwash"));
        storyList.add(new StoryModel(R.drawable.john, "Kushal"));

        recyclerViewStory.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewStory.setAdapter(new StoryAdapter(getContext(), storyList, story -> {}));

        // Setup Profile List
        fullProfileList.add(new ProfileModel(R.drawable.john, "Bishal", 23, "Exploring code, cosmos", "Anime, Music"));
        fullProfileList.add(new ProfileModel(R.drawable.kori, "Bishwash", 23, "Exploring code, cosmos", "Acoustic Music"));
        fullProfileList.add(new ProfileModel(R.drawable.john, "Kushal", 23, "Exploring code, cosmos", "Acoustic Music"));
        filteredProfileList.addAll(fullProfileList);

        adapter = new ProfileAdapter(getContext(), filteredProfileList, matchedUser -> {
            matchViewModel.addMatch(new MatchModel(
                    matchedUser.getName(),
                    matchedUser.getBio(),
                    matchedUser.getImageResId(),
                    matchedUser.getAge()
            ));
        });

        recyclerViewProfiles.setLayoutManager(new NoScrollLinearLayoutManager(getContext()));
        recyclerViewProfiles.setAdapter(adapter);
        recyclerViewProfiles.setClipToPadding(true);
        recyclerViewProfiles.setPadding(0, 0, 0, 0);

        // Search EditText logic
        EditText editTextSearch = view.findViewById(R.id.search_view);

        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterProfiles(editTextSearch.getText().toString());
                editTextSearch.clearFocus();

                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });

        editTextSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProfiles(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Swipe gestures
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                ProfileModel swipedUser = filteredProfileList.get(position);
                ProfileAdapter.ProfileViewHolder holder = (ProfileAdapter.ProfileViewHolder) viewHolder;

                if (direction == ItemTouchHelper.RIGHT) {
                    holder.lottieMatch.setVisibility(View.VISIBLE);
                    holder.lottieMatch.playAnimation();

                    new Handler().postDelayed(() -> {
                        holder.lottieMatch.cancelAnimation();
                        holder.lottieMatch.setVisibility(View.GONE);

                        // 🔔 Toast message for matched
                        Toast.makeText(requireContext(),
                                "Matched with " + swipedUser.getName(), Toast.LENGTH_SHORT).show();

                        //  Add to MatchViewModel
                        matchViewModel.addMatch(new MatchModel(
                                swipedUser.getName(),
                                swipedUser.getBio(),
                                swipedUser.getImageResId(),
                                swipedUser.getAge()
                        ));

                        //  Optional Notification
                        adapter.sendMatchNotification(swipedUser);

                        //  Remove swiped profile
                        removeProfileAtPosition(position, swipedUser);
                    }, 100);
                }
                else {
                    holder.lottieReject.setVisibility(View.VISIBLE);
                    holder.lottieReject.playAnimation();

                    new Handler().postDelayed(() -> {
                        holder.lottieReject.cancelAnimation();
                        holder.lottieReject.setVisibility(View.GONE);
                        removeProfileAtPosition(position, swipedUser);
                    }, 100);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                ProfileAdapter.ProfileViewHolder holder = (ProfileAdapter.ProfileViewHolder) viewHolder;
                holder.lottieMatch.setVisibility(View.GONE);
                holder.lottieReject.setVisibility(View.GONE);
                holder.lottieMatch.cancelAnimation();
                holder.lottieReject.cancelAnimation();
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerViewProfiles);
    }

    private void filterProfiles(String query) {
        try {
            query = query.toLowerCase().trim();
            filteredProfileList.clear();

            if (query.isEmpty()) {
                filteredProfileList.addAll(fullProfileList);
            } else {
                for (ProfileModel profile : fullProfileList) {
                    String name = profile.getName() != null ? profile.getName().toLowerCase() : "";
                    String bio = profile.getBio() != null ? profile.getBio().toLowerCase() : "";

                    if (name.contains(query) || bio.contains(query)) {
                        filteredProfileList.add(profile);
                    }
                }
            }

            adapter.updateList(filteredProfileList);

        } catch (Exception e) {
            e.printStackTrace();
            filteredProfileList.clear();
            filteredProfileList.addAll(fullProfileList);
            adapter.updateList(filteredProfileList);
        }
    }

    private void removeProfileAtPosition(int position, ProfileModel user) {
        filteredProfileList.remove(position);
        fullProfileList.remove(user);
        adapter.updateList(filteredProfileList);
    }
}
