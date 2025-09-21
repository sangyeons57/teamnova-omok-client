package com.example.feature_home.home.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogRequest;
import com.example.core.dialog.MainDialogType;
import com.example.feature_home.R;
import com.example.feature_home.home.presentation.viewmodel.RankingDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

/**
 * Controller showing placeholder ranking data.
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
        bindViews(contentView, dialog, viewModel);

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    private void bindViews(@NonNull View contentView,
                           @NonNull AlertDialog dialog,
                           @NonNull RankingDialogViewModel viewModel) {
        MaterialButton close = contentView.findViewById(R.id.buttonRankingClose);
        MaterialButton daily = contentView.findViewById(R.id.buttonRankingDaily);
        MaterialButton weekly = contentView.findViewById(R.id.buttonRankingWeekly);
        MaterialButton monthly = contentView.findViewById(R.id.buttonRankingMonthly);
        MaterialButton overall = contentView.findViewById(R.id.buttonRankingOverall);
        ViewGroup rankingList = contentView.findViewById(R.id.layoutRankingList);

        populateRankingList(rankingList, viewModel.getRankingEntries());

        close.setOnClickListener(v -> {
            viewModel.onCloseClicked();
            dialog.dismiss();
        });
        daily.setOnClickListener(v -> viewModel.onFilterClicked("daily"));
        weekly.setOnClickListener(v -> viewModel.onFilterClicked("weekly"));
        monthly.setOnClickListener(v -> viewModel.onFilterClicked("monthly"));
        overall.setOnClickListener(v -> viewModel.onFilterClicked("overall"));
    }

    private void populateRankingList(@NonNull ViewGroup root, @NonNull List<String> entries) {
        LayoutInflater inflater = LayoutInflater.from(root.getContext());
        root.removeAllViews();
        for (String entry : entries) {
            MaterialTextView textView = (MaterialTextView) inflater.inflate(R.layout.item_dialog_ranking_entry, root, false);
            textView.setText(entry);
            root.addView(textView);
        }
    }
}
