package com.example.feature_home.home.presentation.adapter;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.designsystem.rule.RuleIconRenderer;
import com.example.feature_home.R;
import com.example.feature_home.home.presentation.model.ScoreMilestone;
import com.example.application.usecase.RuleIconSource;
import com.example.domain.rules.Rule;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScoreMilestoneAdapter extends ListAdapter<ScoreMilestone, ScoreMilestoneAdapter.ScoreViewHolder> {

    public interface OnRuleIconClickListener {
        void onRuleIconClicked(@NonNull String ruleCode);
    }

    private final OnRuleIconClickListener ruleClickListener;
    private Map<String, Rule> ruleCatalog = Collections.emptyMap();
    private Map<String, RuleIconSource> iconSources = Collections.emptyMap();

    public ScoreMilestoneAdapter(@NonNull OnRuleIconClickListener listener) {
        super(DIFF_CALLBACK);
        this.ruleClickListener = listener;
    }

    public void updateRuleCatalog(@NonNull Map<String, Rule> catalog) {
        this.ruleCatalog = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(catalog, "catalog == null")));
        notifyDataSetChanged();
    }

    public void updateIconSources(@NonNull Map<String, RuleIconSource> sources) {
        this.iconSources = Collections.unmodifiableMap(new LinkedHashMap<>(Objects.requireNonNull(sources, "sources == null")));
        notifyDataSetChanged();
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
        holder.bind(getItem(position), ruleCatalog, iconSources);
    }

    private static final DiffUtil.ItemCallback<ScoreMilestone> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull ScoreMilestone oldItem, @NonNull ScoreMilestone newItem) {
            return oldItem.getWindowIndex() == newItem.getWindowIndex();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ScoreMilestone oldItem, @NonNull ScoreMilestone newItem) {
            return oldItem.getScore() == newItem.getScore()
                    && oldItem.getRuleCodes().equals(newItem.getRuleCodes());
        }
    };

    static final class ScoreViewHolder extends RecyclerView.ViewHolder {
        private static final String TAG = "ScoreMilestoneAdapter";

        private final TextView scoreText;
        private final View container;
        private final HorizontalScrollView scrollContainer;
        private final LinearLayout ruleIcons;
        private final OnRuleIconClickListener ruleClickListener;

        ScoreViewHolder(@NonNull View itemView, @NonNull OnRuleIconClickListener listener) {
            super(itemView);
            scoreText = itemView.findViewById(R.id.textScoreValue);
            container = itemView.findViewById(R.id.containerRuleIcons);
            scrollContainer = itemView.findViewById(R.id.scrollMilestoneIcons);
            ruleIcons = itemView.findViewById(R.id.layoutMilestoneRuleIcons);
            this.ruleClickListener = listener;
        }

        void bind(@NonNull ScoreMilestone milestone,
                  @NonNull Map<String, Rule> ruleCatalog,
                  @NonNull Map<String, RuleIconSource> iconSources) {
            int displayScore = Math.round(milestone.getScore());
            scoreText.setText(itemView.getContext().getString(R.string.score_screen_score_format, displayScore));
            List<String> ruleCodes = milestone.getRuleCodes();
            ruleIcons.removeAllViews();
            if (ruleCodes.isEmpty()) {
                ruleIcons.setVisibility(View.GONE);
                scrollContainer.setVisibility(View.GONE);
                if (container != null) {
                    container.setVisibility(View.GONE);
                }
                return;
            }
            ruleIcons.setVisibility(View.VISIBLE);
            scrollContainer.setVisibility(View.VISIBLE);
            if (container != null) {
                container.setVisibility(View.VISIBLE);
            }
            ruleIcons.setGravity(Gravity.END | Gravity.BOTTOM);
            int iconSpacing = (int) (itemView.getResources().getDisplayMetrics().density * 4f);
            for (int i = 0; i < ruleCodes.size(); i++) {
                String ruleCode = ruleCodes.get(i);
                Rule item = ruleCatalog.get(ruleCode);
                RuleIconSource iconSource = iconSources.get(ruleCode);
                Log.d(TAG, "bind: ruleCode=" + ruleCode
                        + " hasRule=" + (item != null)
                        + " iconSource=" + iconSource);
                View iconView = RuleIconRenderer.createIconView(
                        itemView.getContext(),
                        ruleCode,
                        item,
                        iconSource,
                        ruleIcons,
                        false
                );
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                params.gravity = Gravity.CENTER_VERTICAL;
                if (i > 0) {
                    params.setMarginStart(iconSpacing);
                }
                ruleIcons.addView(iconView, params);
                iconView.setOnClickListener(v -> ruleClickListener.onRuleIconClicked(ruleCode));
            }
        }
    }
}
