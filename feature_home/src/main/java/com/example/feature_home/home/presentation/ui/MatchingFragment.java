package com.example.feature_home.home.presentation.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.MatchState;
import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationHostOwner;
import com.example.core.navigation.FragmentNavigationHost;
import com.example.core_di.sound.SoundEffects;
import com.example.feature_home.R;
import com.example.feature_home.home.di.MatchingViewModelFactory;
import com.example.feature_home.home.presentation.state.MatchingViewEvent;
import com.example.feature_home.home.presentation.viewmodel.MatchingViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;

/**
 * Displays the opponent matching screen.
 */
public class MatchingFragment extends Fragment {

    private MatchingViewModel viewModel;
    private FragmentNavigationHost<AppNavigationKey> fragmentNavigationHost;
    private boolean hasNavigatedToGame;

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
        return inflater.inflate(R.layout.fragment_matching, container, false);
    }

    @Override
    public void onDetach() {
        fragmentNavigationHost = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MatchingViewModelFactory factory = MatchingViewModelFactory.create();
        viewModel = new ViewModelProvider(this, factory).get(MatchingViewModel.class);

        Chip statusChip = view.findViewById(R.id.chipMatchStatus);
        MaterialTextView elapsedTimeText = view.findViewById(R.id.textElapsedTime);
        MaterialButton cancelButton = view.findViewById(R.id.buttonCancelMatching);

        setClickWithSound(cancelButton, () -> viewModel.onCancelMatchingClicked());

        observeViewEvents();
        observeMatchState(statusChip, cancelButton);
        observeElapsedTime(elapsedTimeText);
    }

    private void observeViewEvents() {
        viewModel.getViewEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }

            if (event == MatchingViewEvent.RETURN_TO_HOME) {
                returnToHome();
            }

            viewModel.onEventHandled();
        });
    }

    private void observeMatchState(Chip statusChip, MaterialButton cancelButton) {
        viewModel.getMatchState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) {
                return;
            }
            viewModel.onMatchStateUpdated(state);

            if (state == MatchState.MATCHED) {
                statusChip.setText(R.string.matching_status_badge_matched);
                cancelButton.setEnabled(false);
                if (!hasNavigatedToGame) {
                    navigateToGame();
                    hasNavigatedToGame = true;
                }
            } else if (state == MatchState.MATCHING) {
                statusChip.setText(R.string.matching_status_badge_matching);
                cancelButton.setEnabled(true);
            } else {
                statusChip.setText(R.string.matching_status_badge_idle);
                cancelButton.setEnabled(true);
            }
        });
    }

    private void observeElapsedTime(MaterialTextView elapsedTimeText) {
        viewModel.getElapsedTimeText().observe(getViewLifecycleOwner(), elapsedTimeText::setText);
    }

    private void navigateToGame() {
        if (fragmentNavigationHost == null) {
            return;
        }
        fragmentNavigationHost.navigateTo(AppNavigationKey.GAME, true);
    }

    private void returnToHome() {
        hasNavigatedToGame = false;
        if (fragmentNavigationHost == null) {
            return;
        }
        boolean popped = fragmentNavigationHost.popBackStack();
        if (!popped) {
            fragmentNavigationHost.navigateTo(AppNavigationKey.HOME, false);
        }
    }

    private void setClickWithSound(@NonNull View view, @NonNull Runnable action) {
        view.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            action.run();
        });
    }
}
