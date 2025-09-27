package com.example.feature_home.home.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feature_home.R;
import com.example.feature_home.home.presentation.viewmodel.RankingDialogViewModel;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Simple adapter for rendering ranking rows inside the dialog.
 */
public final class RankingDialogAdapter extends ListAdapter<RankingDialogViewModel.RankingRow, RankingDialogAdapter.RankingViewHolder> {

    private static final DiffUtil.ItemCallback<RankingDialogViewModel.RankingRow> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull RankingDialogViewModel.RankingRow oldItem,
                                       @NonNull RankingDialogViewModel.RankingRow newItem) {
            Integer oldRank = oldItem.rank();
            Integer newRank = newItem.rank();
            if (oldRank != null && newRank != null) {
                return Objects.equals(oldRank, newRank);
            }
            return Objects.equals(oldItem.displayName(), newItem.displayName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull RankingDialogViewModel.RankingRow oldItem,
                                          @NonNull RankingDialogViewModel.RankingRow newItem) {
            return Objects.equals(oldItem.rank(), newItem.rank())
                    && Objects.equals(oldItem.displayName(), newItem.displayName())
                    && oldItem.score() == newItem.score();
        }
    };

    private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());

    public RankingDialogAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_dialog_ranking_entry, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        holder.bind(getItem(position), numberFormat);
    }

    static final class RankingViewHolder extends RecyclerView.ViewHolder {

        private final TextView rankText;
        private final TextView nameText;
        private final TextView scoreText;

        RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            this.rankText = itemView.findViewById(R.id.textRankingItemRank);
            this.nameText = itemView.findViewById(R.id.textRankingItemName);
            this.scoreText = itemView.findViewById(R.id.textRankingItemScore);
        }

        void bind(@NonNull RankingDialogViewModel.RankingRow row, @NonNull NumberFormat numberFormat) {
            Integer rank = row.rank();
            String rankLabel = rank != null && rank > 0
                    ? itemView.getContext().getString(R.string.dialog_ranking_rank_format, rank)
                    : itemView.getContext().getString(R.string.dialog_ranking_rank_placeholder);
            rankText.setText(rankLabel);

            nameText.setText(row.displayName());

            String scoreValue = numberFormat.format(row.score());
            String scoreLabel = itemView.getContext().getString(R.string.dialog_ranking_score_format, scoreValue);
            scoreText.setText(scoreLabel);
        }
    }
}
