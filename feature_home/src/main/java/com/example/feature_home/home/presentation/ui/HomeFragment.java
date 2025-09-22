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
import com.example.core.navigation.FragmentNavigatorHost;
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
    private FragmentNavigatorHost<AppNavigationKey> fragmentNavigatorHost;

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DialogHostOwner<?>) {
            dialogHost = ((DialogHostOwner<MainDialogType>) context).getDialogHost();
        } else {
            throw new IllegalStateException("Host must implement DialogHostOwner");
        }

        if (context instanceof FragmentNavigationHostOwner<?> owner) {
            fragmentNavigatorHost = ((FragmentNavigationHostOwner<AppNavigationKey>) owner).getFragmentNavigatorHost();
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
    public void onDetach() {
        dialogHost = null;
        fragmentNavigatorHost = null;
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
        MaterialButton scoreButton = view.findViewById(R.id.buttonScore);
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

        observeViewEvents();
    }

    private void observeViewEvents() {
        viewModel.getViewEvents().observe(getViewLifecycleOwner(), event -> {
            if (event == null) {
                return;
            }

            switch (event) {
                case NAVIGATE_TO_MATCHING -> navigateToMatching();
                case SHOW_GAME_MODE_DIALOG -> enqueueDialog(MainDialogType.GAME_MODE);
                case SHOW_SCORE_DIALOG -> enqueueDialog(MainDialogType.SCORE);
                case SHOW_RANKING_DIALOG -> enqueueDialog(MainDialogType.RANKING);
                case SHOW_SETTING_DIALOG -> enqueueDialog(MainDialogType.SETTING);
            }

            viewModel.onEventHandled();
        });
    }

    private void navigateToMatching() {
        if (fragmentNavigatorHost == null) {
            return;
        }
        fragmentNavigatorHost.navigateTo(AppNavigationKey.MATCHING, FragmentNavigator.Options.builder()
                .addToBackStack(true)
                .tag(AppNavigationKey.MATCHING.name())
                .build());
    }

    private void enqueueDialog(@NonNull MainDialogType type) {
        if (dialogHost != null && dialogHost.isAttached()) {
            dialogHost.enqueue(type);
        }
    }
}
