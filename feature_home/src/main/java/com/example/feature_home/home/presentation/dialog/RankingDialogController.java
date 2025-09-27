package com.example.feature_home.home.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogRequest;
import com.example.core.dialog.MainDialogType;
import com.example.feature_home.R;
import com.example.feature_home.home.presentation.adapter.RankingDialogAdapter;
import com.example.feature_home.home.presentation.viewmodel.RankingDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Controller rendering live ranking data inside the dialog.
 */
public final class RankingDialogController implements DialogController<MainDialogType> {

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_ranking, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        RankingDialogViewModel viewModel = new ViewModelProvider(activity).get(RankingDialogViewModel.class);
        bindViews(activity, contentView, dialog, viewModel);

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    private void bindViews(@NonNull FragmentActivity activity,
                           @NonNull View contentView,
                           @NonNull AlertDialog dialog,
                           @NonNull RankingDialogViewModel viewModel) {
        MaterialButton close = contentView.findViewById(R.id.buttonRankingClose);
        MaterialButton overall = contentView.findViewById(R.id.buttonRankingOverall);
        RecyclerView rankingList = contentView.findViewById(R.id.recyclerRankingList);
        MaterialTextView selfRank = contentView.findViewById(R.id.textSelfRank);
        MaterialTextView selfName = contentView.findViewById(R.id.textSelfName);
        MaterialTextView selfScore = contentView.findViewById(R.id.textSelfScore);
        MaterialTextView emptyView = contentView.findViewById(R.id.textRankingEmpty);

        RankingDialogAdapter adapter = new RankingDialogAdapter();
        rankingList.setLayoutManager(new LinearLayoutManager(contentView.getContext()));
        rankingList.setAdapter(adapter);

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());

        viewModel.getRankingRows().observe(activity, rows -> {
            if (rows == null || rows.isEmpty()) {
                Log.d("RankingDialogController", "Received empty ranking data");
                adapter.submitList(Collections.emptyList());
                rankingList.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                return;
            }
            Log.d("RankingDialogController", "Received new ranking data" + rows.toArray().length);

            adapter.submitList(new ArrayList<>(rows));
            rankingList.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        });
        viewModel.getSelfRow().observe(activity, row -> updateSelfSummary(row, selfRank, selfName, selfScore, numberFormat));

        close.setOnClickListener(v -> {
            viewModel.onCloseClicked();
            dialog.dismiss();
        });
        overall.setOnClickListener(v -> viewModel.refreshRanking());
    }

    private void updateSelfSummary(@Nullable RankingDialogViewModel.RankingRow row,
                                   @NonNull MaterialTextView rankView,
                                   @NonNull MaterialTextView nameView,
                                   @NonNull MaterialTextView scoreView,
                                   @NonNull NumberFormat numberFormat) {
        if (row == null) {
            rankView.setText(rankView.getContext().getString(R.string.dialog_ranking_rank_placeholder));
            nameView.setText("-");
            scoreView.setText(scoreView.getContext().getString(R.string.dialog_ranking_score_format,
                    numberFormat.format(0)));
            return;
        }

        Integer rank = row.rank();
        String rankLabel = rank != null && rank > 0
                ? rankView.getContext().getString(R.string.dialog_ranking_rank_format, rank)
                : rankView.getContext().getString(R.string.dialog_ranking_rank_placeholder);
        rankView.setText(rankLabel);
        nameView.setText(row.displayName());

        String scoreLabel = scoreView.getContext().getString(R.string.dialog_ranking_score_format,
                numberFormat.format(row.score()));
        scoreView.setText(scoreLabel);
    }
}
