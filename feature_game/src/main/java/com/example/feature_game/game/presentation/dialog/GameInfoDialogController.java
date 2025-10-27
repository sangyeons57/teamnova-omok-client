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
import com.example.core_api.dialog.DialogHost;
import com.example.core_api.dialog.DialogHostOwner;
import com.example.core_api.dialog.DialogRequest;
import com.example.core_api.dialog.MainDialogType;
import com.example.designsystem.rule.RuleExplainDialog;
import com.example.designsystem.rule.RuleIconRenderer;
import com.example.feature_game.R;
import com.example.feature_game.game.di.GameInfoDialogViewModelFactory;
import com.example.feature_game.game.presentation.viewmodel.GameInfoDialogViewModel;
import com.example.core_di.sound.SoundEffects;
import com.example.feature_game.game.presentation.viewmodel.GameInfoDialogViewModel.RuleIconState;
import com.example.feature_game.game.presentation.state.GameInfoDialogEvent;
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
        bind(contentView, dialog, viewModel, activity, request);

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
                      @NonNull FragmentActivity activity,
                      @NonNull DialogRequest<MainDialogType> request) {
        MaterialButton closeButton = root.findViewById(R.id.buttonCloseInfo);
        LinearLayout ruleContainer = root.findViewById(R.id.layoutGameRuleIcons);

        viewModel.getEvents().observe(activity, event -> {
            if (event == null) {
                return;
            }
            if (event == GameInfoDialogEvent.DISMISS) {
                dismissThroughHost(activity, request.getType(), dialog);
                viewModel.onEventHandled();
            }
        });

        viewModel.getActiveRuleIcons().observe(activity, rules ->
                populateRuleIcons(ruleContainer, activity, rules));

        closeButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onCloseClicked();
        });
    }

    private void populateRuleIcons(@NonNull LinearLayout container,
                                   @NonNull FragmentActivity activity,
                                   @Nullable List<RuleIconState> rules) {
        container.removeAllViews();
        if (rules == null || rules.isEmpty()) {
            container.setVisibility(View.GONE);
            return;
        }
        container.setVisibility(View.VISIBLE);

        int maxCount = Math.min(rules.size(), 4);
        for (int index = 0; index < maxCount; index++) {
            RuleIconState state = rules.get(index);
            if (state == null) {
                continue;
            }
            String ruleCode = state.getCode();
            boolean allowAsync = state.getRule() == null || state.getIconSource() == null;
            View iconView = RuleIconRenderer.createIconView(
                    activity,
                    ruleCode,
                    state.getRule(),
                    state.getIconSource(),
                    container,
                    allowAsync
            );

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

    private void dismissThroughHost(@NonNull FragmentActivity activity,
                                    @NonNull MainDialogType type,
                                    @NonNull AlertDialog dialog) {
        if (activity instanceof DialogHostOwner<?> owner) {
            @SuppressWarnings("unchecked")
            DialogHost<MainDialogType> host = ((DialogHostOwner<MainDialogType>) owner).getDialogHost();
            if (host != null && host.isAttached()) {
                host.dismiss(type);
                return;
            }
        }
        dialog.dismiss();
    }

    private int dpToPx(@NonNull View view, int dp) {
        float density = view.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
