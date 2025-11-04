package com.example.feature_game.game.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.port.out.realtime.PostGameDecisionAck;
import com.example.application.port.out.realtime.PostGameDecisionOption;
import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogRequest;
import com.example.core_api.dialog.MainDialogType;
import com.example.core_api.navigation.AppNavigationKey;
import com.example.core_api.navigation.FragmentNavigationHost;
import com.example.core_api.navigation.FragmentNavigationHostOwner;
import com.example.feature_game.R;
import com.example.feature_game.game.di.PostGameViewModelFactory;
import com.example.feature_game.game.presentation.model.GameResultOutcome;
import com.example.feature_game.game.presentation.model.PostGameUiState;
import com.example.feature_game.game.presentation.state.PostGameViewEvent;
import com.example.feature_game.game.presentation.viewmodel.PostGameViewModel;
import com.example.core_api.sound.SoundIds;
import com.example.core_di.sound.SoundEffects;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;

/**
 * Dialog controller that presents post-game results and decision actions.
 */
public final class PostGameDialogController implements DialogController<MainDialogType> {

    @Nullable
    private GameResultOutcome lastOutcomeSoundPlayed;

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_post_game, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .setCancelable(false)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        PostGameViewModelFactory factory = PostGameViewModelFactory.create();
        PostGameViewModel viewModel = new ViewModelProvider(activity, factory)
                .get(PostGameViewModel.class);

        viewModel.onDialogShown();

        ViewHolder holder = new ViewHolder(contentView);
        bind(activity, dialog, viewModel, holder);

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    private void bind(@NonNull FragmentActivity activity,
                      @NonNull AlertDialog dialog,
                      @NonNull PostGameViewModel viewModel,
                      @NonNull ViewHolder holder) {
        holder.rematchButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onRematchClicked();
        });
        holder.leaveButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onLeaveClicked();
        });

        Observer<PostGameUiState> stateObserver = state -> renderState(activity, holder, state);
        viewModel.getUiState().observe(activity, stateObserver);

        Observer<PostGameViewEvent> eventObserver = new Observer<>() {
            @Override
            public void onChanged(@Nullable PostGameViewEvent event) {
                if (event == null) {
                    return;
                }
                if (!dialog.isShowing()) {
                    unbindObservers(viewModel, stateObserver, this);
                    return;
                }
                handleEvent(activity, dialog, viewModel, event,
                        () -> unbindObservers(viewModel, stateObserver, this));
            }
        };
        viewModel.getViewEvents().observe(activity, eventObserver);
    }

    private void renderState(@NonNull FragmentActivity activity,
                             @NonNull ViewHolder holder,
                             @Nullable PostGameUiState state) {
        if (state == null) {
            return;
        }
        GameResultOutcome outcome = state.getOutcome();
        switch (outcome) {
            case WIN -> holder.resultText.setText(R.string.game_result_title_win);
            case LOSS -> holder.resultText.setText(R.string.game_result_title_loss);
            case DRAW -> holder.resultText.setText(R.string.game_result_title_draw);
        }
        maybePlayOutcomeSound(outcome);

        holder.durationText.setText(activity.getString(
                R.string.game_result_duration_format,
                formatDuration(state.getDurationMillis())));
        holder.turnsText.setText(activity.getString(
                R.string.game_result_turn_count_format,
                Math.max(0, state.getTurnCount())));

        updateRematchIcons(activity, holder.rematchIconsLayout, state.getRematchCount());

        boolean decisionSubmitted = state.isDecisionSubmitted();
        boolean rematchSelected = state.getSelfDecision() == PostGameDecisionOption.REMATCH;
        boolean leaveSelected = state.getSelfDecision() == PostGameDecisionOption.LEAVE;
        holder.rematchButton.setEnabled(!decisionSubmitted || rematchSelected);
        holder.leaveButton.setEnabled(!decisionSubmitted || leaveSelected);
    }

    private void handleEvent(@NonNull FragmentActivity activity,
                             @NonNull AlertDialog dialog,
                             @NonNull PostGameViewModel viewModel,
                             @Nullable PostGameViewEvent event,
                             @NonNull Runnable cleanup) {
        if (event == null) {
            return;
        }
        if (event.getType() == PostGameViewEvent.Type.SHOW_ERROR) {
            String message = resolveErrorMessage(activity, event);
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
        } else if (event.getType() == PostGameViewEvent.Type.REMATCH_STARTED) {
            navigateTo(activity, AppNavigationKey.MATCHING, true);
            cleanup.run();
            dialog.dismiss();
        } else if (event.getType() == PostGameViewEvent.Type.SESSION_TERMINATED
                || event.getType() == PostGameViewEvent.Type.EXIT_TO_HOME) {
            navigateTo(activity, AppNavigationKey.HOME, false);
            cleanup.run();
            dialog.dismiss();
        }
        viewModel.onEventHandled();
    }

    @NonNull
    private String formatDuration(long durationMillis) {
        long safeMillis = Math.max(0L, durationMillis);
        long totalSeconds = (safeMillis + 500L) / 1_000L;
        long minutes = totalSeconds / 60L;
        long seconds = totalSeconds % 60L;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    private void updateRematchIcons(@NonNull FragmentActivity activity,
                                    @NonNull LinearLayout container,
                                    int count) {
        container.removeAllViews();
        if (count <= 0) {
            container.setVisibility(View.GONE);
            container.setContentDescription(null);
            return;
        }
        int iconSize = dpToPx(activity, 32);
        int margin = dpToPx(activity, 8);
        for (int i = 0; i < count; i++) {
            ImageView icon = new ImageView(activity);
            icon.setImageResource(R.drawable.ic_person_outline);
            icon.setContentDescription(activity.getString(R.string.post_game_rematch_icon_content_description));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iconSize, iconSize);
            if (i < count - 1) {
                params.setMarginEnd(margin);
            }
            container.addView(icon, params);
        }
        container.setVisibility(View.VISIBLE);
        container.setContentDescription(activity.getResources().getQuantityString(
                R.plurals.post_game_rematch_icon_count_description, count, count));
    }

    private void navigateTo(@NonNull FragmentActivity activity,
                            @NonNull AppNavigationKey key,
                            boolean addToBackStack) {
        FragmentNavigationHost<AppNavigationKey> host = resolveNavigationHost(activity);
        if (host == null) {
            return;
        }
        if (key == AppNavigationKey.HOME) {
            host.clearBackStack();
        }
        host.navigateTo(key, addToBackStack);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private FragmentNavigationHost<AppNavigationKey> resolveNavigationHost(@NonNull FragmentActivity activity) {
        if (activity instanceof FragmentNavigationHostOwner<?> owner) {
            return ((FragmentNavigationHostOwner<AppNavigationKey>) owner).getFragmentNavigatorHost();
        }
        return null;
    }

    @NonNull
    private String resolveErrorMessage(@NonNull FragmentActivity activity,
                                       @NonNull PostGameViewEvent event) {
        PostGameDecisionAck.ErrorReason reason = event.getErrorReason();
        if (reason == null) {
            String message = event.getMessage();
            return message != null && !message.isEmpty()
                    ? message
                    : activity.getString(R.string.post_game_error_generic);
        }
        return switch (reason) {
            case INVALID_PLAYER -> activity.getString(R.string.post_game_error_invalid_player);
            case ALREADY_DECIDED -> activity.getString(R.string.post_game_error_already_decided);
            case TIME_WINDOW_CLOSED -> activity.getString(R.string.post_game_error_time_window_closed);
            case SESSION_CLOSED -> activity.getString(R.string.post_game_error_session_closed);
            case SESSION_NOT_FOUND -> activity.getString(R.string.post_game_error_session_not_found);
            case INVALID_PAYLOAD -> activity.getString(R.string.post_game_error_invalid_payload);
            case NONE, UNKNOWN -> {
                String message = event.getMessage();
                if (message != null && !message.isEmpty()) {
                    yield message;
                }
                yield activity.getString(R.string.post_game_error_generic);
            }
        };
    }

    private void unbindObservers(@NonNull PostGameViewModel viewModel,
                                 @NonNull Observer<PostGameUiState> stateObserver,
                                 @NonNull Observer<PostGameViewEvent> eventObserver) {
        viewModel.getUiState().removeObserver(stateObserver);
        viewModel.getViewEvents().removeObserver(eventObserver);
    }

    private int dpToPx(@NonNull FragmentActivity activity, float dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                activity.getResources().getDisplayMetrics()));
    }

    private void maybePlayOutcomeSound(@NonNull GameResultOutcome outcome) {
        if (outcome == lastOutcomeSoundPlayed) {
            return;
        }
        if (outcome == GameResultOutcome.WIN) {
            SoundEffects.play(SoundIds.GAME_SIMPLE_WIN);
        } else if (outcome == GameResultOutcome.LOSS) {
            SoundEffects.play(SoundIds.GAME_SIMPLE_DEFEAT);
        }
        lastOutcomeSoundPlayed = outcome;
    }

    private static final class ViewHolder {

        private final MaterialTextView resultText;
        private final MaterialTextView durationText;
        private final MaterialTextView turnsText;
        private final LinearLayout rematchIconsLayout;
        private final MaterialButton rematchButton;
        private final MaterialButton leaveButton;

        private ViewHolder(@NonNull View root) {
            resultText = root.findViewById(R.id.textPostGameResult);
            durationText = root.findViewById(R.id.textGameDuration);
            turnsText = root.findViewById(R.id.textGameTurns);
            rematchIconsLayout = root.findViewById(R.id.layoutRematchIcons);
            rematchButton = root.findViewById(R.id.buttonPostGameRematch);
            leaveButton = root.findViewById(R.id.buttonPostGameLeave);
        }
    }
}
