package com.example.feature_game.game.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogRequest;
import com.example.core_api.dialog.MainDialogType;
import com.example.designsystem.rule.RuleExplainDialog;
import com.example.designsystem.rule.RuleIconRenderer;
import com.example.feature_game.R;
import com.example.feature_game.game.di.GameInfoDialogViewModelFactory;
import com.example.feature_game.game.presentation.viewmodel.GameInfoDialogViewModel;
import com.example.core_di.sound.SoundEffects;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

/**
 * Shows a translucent overlay with quick game actions.
 */
public final class GameInfoDialogController implements DialogController<MainDialogType> {

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_game_info, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .setCancelable(true)
                .create();

        GameInfoDialogViewModelFactory factory = GameInfoDialogViewModelFactory.create();
        GameInfoDialogViewModel viewModel = new ViewModelProvider(activity, factory)
                .get(GameInfoDialogViewModel.class);
        bind(contentView, dialog, viewModel, activity);

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    private void bind(@NonNull View root,
                      @NonNull AlertDialog dialog,
                      @NonNull GameInfoDialogViewModel viewModel,
                      @NonNull FragmentActivity activity) {
        MaterialButton closeButton = root.findViewById(R.id.buttonCloseInfo);
        LinearLayout ruleContainer = root.findViewById(R.id.layoutGameRuleIcons);

        viewModel.getDismissEvent().observe(activity, dismiss -> {
            if (dismiss) {
                dialog.dismiss();
                viewModel.onEventHandled();
            }
        });

        viewModel.getActiveRuleCodes().observe(activity, ruleCodes ->
                populateRuleIcons(ruleContainer, activity, ruleCodes));

        closeButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onCloseClicked();
        });
    }

    private void populateRuleIcons(@NonNull LinearLayout container,
                                   @NonNull FragmentActivity activity,
                                   @Nullable List<String> ruleCodes) {
        container.removeAllViews();
        if (ruleCodes == null || ruleCodes.isEmpty()) {
            container.setVisibility(View.GONE);
            return;
        }
        container.setVisibility(View.VISIBLE);

        int maxCount = Math.min(ruleCodes.size(), 4);
        for (int index = 0; index < maxCount; index++) {
            String ruleCode = ruleCodes.get(index);
            View iconView = RuleIconRenderer.createIconView(activity, ruleCode, container);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            if (index < maxCount - 1) {
                params.setMarginEnd(dpToPx(container, 12));
            }
            container.addView(iconView, params);

            iconView.setOnClickListener(v -> {
                SoundEffects.playButtonClick();
                RuleExplainDialog.present(activity.getSupportFragmentManager(), ruleCode);
            });
        }
    }

    private int dpToPx(@NonNull View view, int dp) {
        float density = view.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
