package com.example.feature_game.game.presentation.dialog;

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
import com.example.feature_game.R;
import com.example.feature_game.game.di.GameResultDialogViewModelFactory;
import com.example.feature_game.game.presentation.model.GameResultOutcome;
import com.example.feature_game.game.presentation.model.GameResultUiState;
import com.example.feature_game.game.presentation.state.GameResultDialogEvent;
import com.example.feature_game.game.presentation.viewmodel.GameResultDialogViewModel;
import com.example.core_di.sound.SoundEffects;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Renders the game result dialog showing outcome, duration, and rematch controls.
 */
public final class GameResultDialogController implements DialogController<MainDialogType> {

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_game_result, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .setCancelable(false)
                .create();

        GameResultDialogViewModelFactory factory = GameResultDialogViewModelFactory.create();
        GameResultDialogViewModel viewModel = new ViewModelProvider(activity, factory)
                .get(GameResultDialogViewModel.class);
        bind(contentView, dialog, viewModel, activity);

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B3000000")));
            }
        });
        return dialog;
    }

    private void bind(@NonNull View root,
                      @NonNull AlertDialog dialog,
                      @NonNull GameResultDialogViewModel viewModel,
                      @NonNull FragmentActivity activity) {
        MaterialTextView resultText = root.findViewById(R.id.textGameResult);
        MaterialTextView durationText = root.findViewById(R.id.textGameDuration);
        MaterialTextView turnCountText = root.findViewById(R.id.textGameTurnCount);
        MaterialTextView rematchVotesText = root.findViewById(R.id.textRematchVotes);
        MaterialButton exitButton = root.findViewById(R.id.buttonExitGame);
        MaterialButton rematchButton = root.findViewById(R.id.buttonRematch);

        viewModel.getUiState().observe(activity, state -> {
            if (state == null) {
                return;
            }
            bindState(activity, state, resultText, durationText, turnCountText, rematchVotesText, rematchButton);
        });

        viewModel.getEvents().observe(activity, event -> {
            if (event == null) {
                return;
            }
            if (event == GameResultDialogEvent.DISMISS) {
                dialog.dismiss();
            }
            viewModel.onEventHandled();
        });

        exitButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onExitClicked();
        });
        rematchButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onRematchClicked();
        });
    }

    private void bindState(@NonNull FragmentActivity activity,
                           @NonNull GameResultUiState state,
                           @NonNull MaterialTextView resultText,
                           @NonNull MaterialTextView durationText,
                           @NonNull MaterialTextView turnCountText,
                           @NonNull MaterialTextView rematchVotesText,
                           @NonNull MaterialButton rematchButton) {
        int resultLabel;
        switch (state.getOutcome()) {
            case WIN:
                resultLabel = R.string.game_result_title_win;
                break;
            case LOSS:
                resultLabel = R.string.game_result_title_loss;
                break;
            case DRAW:
                resultLabel = R.string.game_result_title_draw;
                break;
            default:
                throw new IllegalStateException("Unhandled outcome: " + state.getOutcome());
        }
        resultText.setText(resultLabel);

        String duration = formatDuration(state.getDurationMillis());
        String durationLabel = activity.getString(R.string.game_result_duration_prefix) + duration;
        durationText.setText(durationLabel);

        String turnLabel = activity.getString(R.string.game_result_turn_count_prefix)
                + state.getTurnCount()
                + activity.getString(R.string.game_result_turn_count_suffix);
        turnCountText.setText(turnLabel);

        String votesLabel = activity.getString(R.string.game_result_rematch_votes_prefix)
                + state.getRematchVotes()
                + activity.getString(R.string.game_result_rematch_votes_suffix);
        rematchVotesText.setText(votesLabel);
        if (state.isRematchRequested()) {
            rematchButton.setText(R.string.game_result_rematch_cancel_button);
        } else {
            rematchButton.setText(R.string.game_result_rematch_button);
        }
    }

    @NonNull
    private String formatDuration(long millis) {
        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }
}
