package com.example.feature_home.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogRequest;
import com.example.core_api.dialog.MainDialogType;
import com.example.core_di.sound.SoundEffects;
import com.example.feature_home.R;
import com.example.feature_home.presentation.viewmodel.ScoreDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

/**
 * Controller that presents a score summary dialog.
 */
public final class ScoreDialogController implements DialogController<MainDialogType> {

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_score, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        ScoreDialogViewModel viewModel = new ViewModelProvider(activity).get(ScoreDialogViewModel.class);
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
                           @NonNull ScoreDialogViewModel viewModel) {
        MaterialButton close = contentView.findViewById(R.id.buttonScoreClose);
        MaterialButton share = contentView.findViewById(R.id.buttonScoreShare);
        ViewGroup scoreList = contentView.findViewById(R.id.layoutScoreList);

        populateScores(scoreList, viewModel.getScores());

        close.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onCloseClicked();
            dialog.dismiss();
        });
        share.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onShareClicked();
        });
    }

    private void populateScores(@NonNull ViewGroup root, @NonNull List<String> scores) {
        LayoutInflater inflater = LayoutInflater.from(root.getContext());
        root.removeAllViews();
        for (String score : scores) {
            MaterialTextView textView = (MaterialTextView) inflater.inflate(R.layout.item_dialog_ranking_entry, root, false);
            textView.setText(score);
            root.addView(textView);
        }
    }
}
