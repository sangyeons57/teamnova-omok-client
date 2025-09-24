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

import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationHostOwner;
import com.example.core.navigation.FragmentNavigator;
import com.example.core.navigation.FragmentNavigationHost;
import com.example.feature_home.R;
import com.example.feature_home.home.di.MatchingViewModelFactory;
import com.example.feature_home.home.presentation.state.MatchingViewEvent;
import com.example.feature_home.home.presentation.viewmodel.MatchingViewModel;
import com.google.android.material.button.MaterialButton;

/**
 * Displays the opponent matching screen.
 */
public class MatchingFragment extends Fragment {

    private MatchingViewModel viewModel;
    private FragmentNavigationHost<AppNavigationKey> fragmentNavigationHost;

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
        if (fragmentNavigationHost == null) {
            return;
        }
        boolean popped = fragmentNavigationHost.popBackStack();
        if (!popped) {
            fragmentNavigationHost.navigateTo(AppNavigationKey.HOME, false);
        }
    }
}
