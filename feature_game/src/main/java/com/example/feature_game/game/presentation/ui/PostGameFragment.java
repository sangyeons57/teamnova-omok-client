package com.example.feature_game.game.presentation.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.port.out.realtime.PostGameDecisionAck;
import com.example.application.port.out.realtime.PostGameDecisionOption;
import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationHost;
import com.example.core.navigation.FragmentNavigationHostOwner;
import com.example.feature_game.R;
import com.example.feature_game.game.di.PostGameViewModelFactory;
import com.example.feature_game.game.presentation.model.PostGameUiState;
import com.example.feature_game.game.presentation.state.PostGameViewEvent;
import com.example.feature_game.game.presentation.viewmodel.PostGameViewModel;
import com.example.core_di.sound.SoundEffects;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Locale;


/**
 * Displays post-game results and rematch/leave actions.
 */
public final class PostGameFragment extends Fragment {

    private FragmentNavigationHost<AppNavigationKey> fragmentNavigationHost;
    private PostGameViewModel viewModel;

    private MaterialTextView resultText;
    private MaterialTextView durationText;
    private MaterialTextView turnsText;
    private LinearLayout rematchIconsLayout;
    private MaterialButton rematchButton;
    private MaterialButton leaveButton;

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof FragmentNavigationHostOwner<?> owner) {
            fragmentNavigationHost = ((FragmentNavigationHostOwner<AppNavigationKey>) owner).getFragmentNavigatorHost();
        } else {
            throw new IllegalStateException("Host must provide FragmentNavigatorHost");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PostGameViewModelFactory factory = PostGameViewModelFactory.create();
        viewModel = new ViewModelProvider(this, factory).get(PostGameViewModel.class);
        bindViews(view);
        observeViewModel();
    }

    private void bindViews(@NonNull View root) {
        resultText = root.findViewById(R.id.textPostGameResult);
        durationText = root.findViewById(R.id.textGameDuration);
        turnsText = root.findViewById(R.id.textGameTurns);
        rematchIconsLayout = root.findViewById(R.id.layoutRematchIcons);
        rematchButton = root.findViewById(R.id.buttonPostGameRematch);
        leaveButton = root.findViewById(R.id.buttonPostGameLeave);

        rematchButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onRematchClicked();
        });
        leaveButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onLeaveClicked();
        });
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(getViewLifecycleOwner(), this::renderState);
        viewModel.getViewEvents().observe(getViewLifecycleOwner(), this::handleEvent);
    }

    private void renderState(@NonNull PostGameUiState state) {
        switch (state.getOutcome()) {
            case WIN -> resultText.setText(R.string.game_result_title_win);
            case LOSS -> resultText.setText(R.string.game_result_title_loss);
            case DRAW -> resultText.setText(R.string.game_result_title_draw);
        }

        durationText.setText(getString(R.string.game_result_duration_format, formatDuration(state.getDurationMillis())));
        turnsText.setText(getString(R.string.game_result_turn_count_format, Math.max(0, state.getTurnCount())));
        updateRematchIcons(state.getRematchCount());

        boolean decisionSubmitted = state.isDecisionSubmitted();
        rematchButton.setEnabled(!decisionSubmitted || state.getSelfDecision() == PostGameDecisionOption.REMATCH);
        leaveButton.setEnabled(!decisionSubmitted || state.getSelfDecision() == PostGameDecisionOption.LEAVE);
    }

    private void handleEvent(@Nullable PostGameViewEvent event) {
        if (event == null) {
            return;
        }
        if (event.getType() == PostGameViewEvent.Type.SHOW_ERROR) {
            String message = resolveErrorMessage(event);
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        } else if (event.getType() == PostGameViewEvent.Type.REMATCH_STARTED) {
            navigateTo(AppNavigationKey.MATCHING, true);
        } else if (event.getType() == PostGameViewEvent.Type.SESSION_TERMINATED) {
            navigateTo(AppNavigationKey.HOME, false);
        } else if (event.getType() == PostGameViewEvent.Type.EXIT_TO_HOME) {
            navigateTo(AppNavigationKey.HOME, false);
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

    private void updateRematchIcons(int count) {
        LinearLayout container = rematchIconsLayout;
        if (container == null) {
            return;
        }
        container.removeAllViews();
        if (count <= 0) {
            container.setVisibility(View.GONE);
            container.setContentDescription(null);
            return;
        }
        int iconSize = dpToPx(32);
        int margin = dpToPx(8);
        for (int i = 0; i < count; i++) {
            ImageView icon = new ImageView(container.getContext());
            icon.setImageResource(R.drawable.ic_person_outline);
            icon.setContentDescription(getString(R.string.post_game_rematch_icon_content_description));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iconSize, iconSize);
            if (i < count - 1) {
                params.setMarginEnd(margin);
            }
            container.addView(icon, params);
        }
        container.setVisibility(View.VISIBLE);
        container.setContentDescription(getResources().getQuantityString(
                R.plurals.post_game_rematch_icon_count_description, count, count));
    }

    private void navigateTo(@NonNull AppNavigationKey key, boolean addToBackStack) {
        FragmentNavigationHost<AppNavigationKey> host = fragmentNavigationHost;
        if (host == null) {
            return;
        }
        if (key == AppNavigationKey.HOME) {
            host.clearBackStack();
        }
        host.navigateTo(key, addToBackStack);
    }

    @NonNull
    private String resolveErrorMessage(@NonNull PostGameViewEvent event) {
        PostGameDecisionAck.ErrorReason reason = event.getErrorReason();
        if (reason == null) {
            String message = event.getMessage();
            return message != null && !message.isEmpty()
                    ? message
                    : getString(R.string.post_game_error_generic);
        }
        return switch (reason) {
            case INVALID_PLAYER -> getString(R.string.post_game_error_invalid_player);
            case ALREADY_DECIDED -> getString(R.string.post_game_error_already_decided);
            case TIME_WINDOW_CLOSED -> getString(R.string.post_game_error_time_window_closed);
            case SESSION_CLOSED -> getString(R.string.post_game_error_session_closed);
            case SESSION_NOT_FOUND -> getString(R.string.post_game_error_session_not_found);
            case INVALID_PAYLOAD -> getString(R.string.post_game_error_invalid_payload);
            case NONE, UNKNOWN -> {
                String message = event.getMessage();
                if (message != null && !message.isEmpty()) {
                    yield message;
                }
                yield getString(R.string.post_game_error_generic);
            }
        };
    }

    private int dpToPx(float dp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics()));
    }

    @Override
    public void onDetach() {
        fragmentNavigationHost = null;
        super.onDetach();
    }
}
