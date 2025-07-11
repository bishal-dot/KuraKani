package com.example.kurakani.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.R;
import com.example.kurakani.fragments.ProfileMatchDetail;
import com.example.kurakani.viewmodel.MatchModel;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private final List<MatchModel> matchList;
    private final Context context;
    private OnMatchClickListener matchClickListener;

    public MatchAdapter(Context context, List<MatchModel> matchList) {
        this.context = context;
        this.matchList = matchList;
    }

    public void setOnMatchClickListener(OnMatchClickListener listener) {
        this.matchClickListener = listener;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        MatchModel match = matchList.get(position);
        holder.nameAge.setText(match.name + ", " + match.age);
        holder.bio.setText(match.bio);
        holder.avatar.setImageResource(match.avatarResId);

        holder.itemView.setOnClickListener(v -> {
            Fragment detailFragment = new ProfileMatchDetail();
            Bundle bundle = new Bundle();
            bundle.putString("name", match.name);
            bundle.putString("bio", match.bio);
            bundle.putInt("age", match.age);
            bundle.putInt("avatar", match.avatarResId);
            detailFragment.setArguments(bundle);

            ((AppCompatActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, detailFragment)
                    .addToBackStack(null)
                    .commit();

            if (matchClickListener != null) {
                matchClickListener.onMatchClicked(match);
            }
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {
        TextView nameAge, bio;
        ImageView avatar;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            nameAge = itemView.findViewById(R.id.textNameAge);
            bio = itemView.findViewById(R.id.textBio);
            avatar = itemView.findViewById(R.id.profileAvatar);
        }
    }

    public interface OnMatchClickListener {
        void onMatchClicked(MatchModel user);
    }
}