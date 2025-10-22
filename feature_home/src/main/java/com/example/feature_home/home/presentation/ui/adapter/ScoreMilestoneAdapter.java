package com.example.feature_home.home.presentation.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.designsystem.rule.RuleIconRenderer;
import com.example.feature_home.R;
import com.example.feature_home.home.presentation.model.ScoreMilestone;

import java.util.List;

public class ScoreMilestoneAdapter extends ListAdapter<ScoreMilestone, ScoreMilestoneAdapter.ScoreViewHolder> {

    public interface OnRuleIconClickListener {
        void onRuleIconClicked(int ruleId);
    }

    private final OnRuleIconClickListener ruleClickListener;

    public ScoreMilestoneAdapter(@NonNull OnRuleIconClickListener listener) {
        super(DIFF_CALLBACK);
        this.ruleClickListener = listener;
    }

    @NonNull
    @Override
    public ScoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_score_milestone, parent, false);
        return new ScoreViewHolder(view, ruleClickListener);
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
            return oldItem.getScore() == newItem.getScore()
                    && oldItem.getRuleIds().equals(newItem.getRuleIds());
        }
    };

    static final class ScoreViewHolder extends RecyclerView.ViewHolder {

        private final TextView scoreText;
        private final LinearLayout ruleIcons;
        private final OnRuleIconClickListener ruleClickListener;

        ScoreViewHolder(@NonNull View itemView, @NonNull OnRuleIconClickListener listener) {
            super(itemView);
            scoreText = itemView.findViewById(R.id.textScoreValue);
            ruleIcons = itemView.findViewById(R.id.layoutMilestoneRuleIcons);
            this.ruleClickListener = listener;
        }

        void bind(@NonNull ScoreMilestone milestone) {
            int displayScore = Math.round(milestone.getScore());
            scoreText.setText(itemView.getContext().getString(R.string.score_screen_score_format, displayScore));
            List<Integer> ruleIds = milestone.getRuleIds();
            ruleIcons.removeAllViews();
            if (ruleIds == null || ruleIds.isEmpty()) {
                ruleIcons.setVisibility(View.GONE);
                return;
            }
            ruleIcons.setVisibility(View.VISIBLE);
            int count = Math.min(ruleIds.size(), 1);
            for (int i = 0; i < count; i++) {
                int ruleId = ruleIds.get(i);
                View iconView = RuleIconRenderer.createIconView(itemView.getContext(), ruleId, ruleIcons);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                ruleIcons.addView(iconView, params);
                iconView.setOnClickListener(v -> ruleClickListener.onRuleIconClicked(ruleId));
            }
        }

        private int dpToPx(int dp) {
            float density = itemView.getResources().getDisplayMetrics().density;
            return Math.round(dp * density);
        }
    }
}
