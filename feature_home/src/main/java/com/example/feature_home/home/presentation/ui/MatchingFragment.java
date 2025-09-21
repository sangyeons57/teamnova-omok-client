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

import com.example.core.navigation.NavigationHelper;
import com.example.feature_home.R;
import com.example.feature_home.home.di.HomeDependencyProvider;
import com.example.feature_home.home.di.MatchingViewModelFactory;
import com.example.feature_home.home.presentation.state.MatchingViewEvent;
import com.example.feature_home.home.presentation.viewmodel.MatchingViewModel;
import com.google.android.material.button.MaterialButton;

/**
 * Displays the opponent matching screen.
 */
public class MatchingFragment extends Fragment {

    private MatchingViewModel viewModel;
    private HomeDependencyProvider dependencyProvider;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeDependencyProvider provider) {
            dependencyProvider = provider;
        } else {
            throw new IllegalStateException("Host must implement HomeDependencyProvider");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_matching, container, false);
    }

    @Override
    public void onDetach() {
        dependencyProvider = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MatchingViewModelFactory factory = MatchingViewModelFactory.create();
        viewModel = new ViewModelProvider(this, factory).get(MatchingViewModel.class);

        MaterialButton bannerButton = view.findViewById(R.id.buttonMatchingBanner);
        MaterialButton returnHomeButton = view.findViewById(R.id.buttonReturnHome);

        bannerButton.setOnClickListener(v -> viewModel.onBannerClicked());
        returnHomeButton.setOnClickListener(v -> viewModel.onReturnHomeClicked());

        observeViewEvents();
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

    private void returnToHome() {
        NavigationHelper helper = dependencyProvider.getNavigationHelper();
        boolean popped = helper.popBackStack();
        if (!popped) {
            helper.navigateTo(new HomeFragment(), NavigationHelper.NavigationOptions.builder()
                    .addToBackStack(false)
                    .tag("Home")
                    .build());
        }
    }
}
