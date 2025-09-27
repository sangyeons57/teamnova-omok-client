package com.example.feature_home.home.presentation.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feature_home.R;
import com.example.feature_home.home.presentation.model.ScoreMilestone;

public class ScoreMilestoneAdapter extends ListAdapter<ScoreMilestone, ScoreMilestoneAdapter.ScoreViewHolder> {

    public ScoreMilestoneAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_score_milestone, parent, false);
        return new ScoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    private static final DiffUtil.ItemCallback<ScoreMilestone> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ScoreMilestone oldItem, @NonNull ScoreMilestone newItem) {
            return oldItem.getScore() == newItem.getScore();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ScoreMilestone oldItem, @NonNull ScoreMilestone newItem) {
            return oldItem.getScore() == newItem.getScore() && oldItem.getIconRes() == newItem.getIconRes();
        }
    };

    static final class ScoreViewHolder extends RecyclerView.ViewHolder {

        private final TextView scoreText;
        private final ImageView scoreIcon;
        ScoreViewHolder(@NonNull View itemView) {
            super(itemView);
            scoreText = itemView.findViewById(R.id.textScoreValue);
            scoreIcon = itemView.findViewById(R.id.imageScoreIcon);
        }

        void bind(@NonNull ScoreMilestone milestone) {
            int displayScore = Math.round(milestone.getScore());
            scoreText.setText(itemView.getContext().getString(R.string.score_screen_score_format, displayScore));
            scoreIcon.setImageResource(milestone.getIconRes());
        }
    }
}
