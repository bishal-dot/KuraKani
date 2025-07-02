package com.example.kurakani;

import android.content.Context;
import android.content.Intent;
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

import com.example.kurakani.fragments.ProfileMatchDetail;
import com.example.kurakani.model.Match;

import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {
    private List<Match> matchList;
    private Context context;

    public MatchAdapter(Context context, List<Match> matchList) {
        this.context = context;
        this.matchList = matchList;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);

        // Set name + age in one TextView
        String nameAgeText = match.name + ", " + match.age;
        holder.nameAge.setText(nameAgeText);

        holder.bio.setText(match.bio);
        holder.avatar.setImageResource(match.avatarResId);

        // Navigate to MatchDetailActivity on click
        holder.itemView.setOnClickListener(v -> {
            Fragment detailFragment = new ProfileMatchDetail();
            Bundle bundle = new Bundle();
            bundle.putString("name" , match.name);
            bundle.putString("bio" , match.bio);
            bundle.putInt("age" , match.age);
            bundle.putInt("avatar" , match.avatarResId);
            detailFragment.setArguments(bundle);

            ((AppCompatActivity) context).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer,detailFragment)
                    .addToBackStack(null)
                    .commit();
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
}