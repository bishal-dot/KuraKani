package com.example.kurakani.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kurakani.R;
import com.example.kurakani.viewmodel.MatchesModel;

import java.util.List;

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.ViewHolder> {

    private Context context;
    private List<MatchesModel> matchesList;

    public MatchesAdapter(Context context, List<MatchesModel> matchesList) {
        this.context = context;
        this.matchesList = matchesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.match_item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MatchesModel match = matchesList.get(position);

        // Bind MatchesModel fields to UI
        holder.tvMatchTitle.setText(match.getName() != null ? match.getName() : "Unknown"); // User's name
        holder.tvMatchStatus.setText(match.getStatus() != null ? match.getStatus() : "Pending"); // Matched or Pending
        // Change status text color based on status
        if ("Matched".equalsIgnoreCase(match.getStatus())) {
            holder.tvMatchStatus.setTextColor(Color.GREEN);
        } else {
            holder.tvMatchStatus.setTextColor(Color.YELLOW);
        }
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMatchTitle, tvMatchStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMatchTitle = itemView.findViewById(R.id.tvMatchTitle);
            tvMatchStatus = itemView.findViewById(R.id.tvMatchStatus);
        }
    }
}
