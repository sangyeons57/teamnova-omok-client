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

import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogHostOwner;
import com.example.core.dialog.MainDialogType;
import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationHostOwner;
import com.example.core.navigation.FragmentNavigator;
import com.example.core.navigation.FragmentNavigationHost;
import com.example.application.session.GameMode;
import com.example.domain.user.entity.User;
import com.example.feature_home.R;
import com.example.feature_home.home.di.HomeViewModelFactory;
import com.example.feature_home.home.presentation.viewmodel.HomeViewModel;
import com.google.android.material.button.MaterialButton;

/**
 * Displays the main Home UI and delegates user interactions to the ViewModel.
 */
public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;
    private DialogHost<MainDialogType> dialogHost;
    private FragmentNavigationHost<AppNavigationKey> fragmentNavigationHost;
    private MaterialButton scoreButton;

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DialogHostOwner<?>) {
            dialogHost = ((DialogHostOwner<MainDialogType>) context).getDialogHost();
        } else {
            throw new IllegalStateException("Host must implement DialogHostOwner");
        }

        if (context instanceof FragmentNavigationHostOwner<?>) {
            FragmentNavigationHostOwner<AppNavigationKey> owner = (FragmentNavigationHostOwner<AppNavigationKey>) context;
            fragmentNavigationHost = owner.getFragmentNavigatorHost();
        } else {
            throw new IllegalStateException("Host must provide FragmentNavigatorHost");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refreshSelfProfile();
        }
    }

    @Override
    public void onDetach() {
        dialogHost = null;
        fragmentNavigationHost = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HomeViewModelFactory factory = HomeViewModelFactory.create();
        viewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        MaterialButton bannerButton = view.findViewById(R.id.buttonBannerAd);
        MaterialButton matchingButton = view.findViewById(R.id.buttonMatching);
        MaterialButton gameModeButton = view.findViewById(R.id.buttonGameMode);
        scoreButton = view.findViewById(R.id.buttonScore);
        MaterialButton rankingButton = view.findViewById(R.id.buttonRanking);
        MaterialButton settingsButton = view.findViewById(R.id.buttonSettings);
        MaterialButton logOnlyButton = view.findViewById(R.id.buttonLogOnly);

        bannerButton.setOnClickListener(v -> viewModel.onBannerClicked());
        matchingButton.setOnClickListener(v -> viewModel.onMatchingClicked());
        gameModeButton.setOnClickListener(v -> viewModel.onGameModeClicked());
        scoreButton.setOnClickListener(v -> viewModel.onScoreClicked());
        rankingButton.setOnClickListener(v -> viewModel.onRankingClicked());
        settingsButton.setOnClickListener(v -> viewModel.onSettingsClicked());
        logOnlyButton.setOnClickListener(v -> viewModel.onLogOnlyClicked());
        viewModel.getGameMode().observe(getViewLifecycleOwner(), mode -> {
            String label = getString(gameModeToLabel(mode));
            gameModeButton.setText(label);
            gameModeButton.setContentDescription(getString(R.string.home_game_mode_content_description, label));
        });
        observeUserScore();

        observeViewEvents();
    }

    private void observeUserScore() {
        updateScoreButton(null);
        viewModel.getUser().observe(getViewLifecycleOwner(), this::updateScoreButton);
    }

    private void updateScoreButton(@Nullable User user) {
        if (scoreButton == null) {
            return;
        }
        int scoreValue = 0;
        if (user != null && user.getScore() != null) {
            scoreValue = user.getScore().getValue();
        }
        String label = getString(R.string.home_score_button_format, scoreValue);
        scoreButton.setText(label);
        scoreButton.setContentDescription(label);
    }

    private void observeViewEvents() {
        viewModel.getViewEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }

            switch (event) {
                case NAVIGATE_TO_MATCHING:
                    navigateToMatching();
                    break;
                case SHOW_GAME_MODE_DIALOG:
                    enqueueDialog(MainDialogType.GAME_MODE);
                    break;
                case NAVIGATE_TO_SCORE:
                    navigateToScore();
                    break;
                case SHOW_RANKING_DIALOG:
                    enqueueDialog(MainDialogType.RANKING);
                    break;
                case SHOW_SETTING_DIALOG:
                    enqueueDialog(MainDialogType.SETTING);
                    break;
                default:
                    break;
            }

            viewModel.onEventHandled();
        });
    }

    private int gameModeToLabel(@NonNull GameMode mode) {
        switch (mode) {
            case FREE:
                return R.string.game_mode_free;
            case TWO_PLAYER:
                return R.string.game_mode_two_player;
            case THREE_PLAYER:
                return R.string.game_mode_three_player;
            case FOUR_PLAYER:
                return R.string.game_mode_four_player;
            default:
                throw new IllegalStateException("Unknown game mode: " + mode);
        }
    }

    private void navigateToMatching() {
        if (fragmentNavigationHost == null) {
            return;
        }
        fragmentNavigationHost.navigateTo(AppNavigationKey.MATCHING, true);
    }

    private void navigateToScore() {
        if (fragmentNavigationHost == null) {
            return;
        }
        fragmentNavigationHost.navigateTo(AppNavigationKey.SCORE, true);
    }

    private void enqueueDialog(@NonNull MainDialogType type) {
        if (dialogHost != null && dialogHost.isAttached()) {
            dialogHost.enqueue(type);
        }
    }
}
