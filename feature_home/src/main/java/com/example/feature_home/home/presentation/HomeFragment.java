package com.example.feature_home.home.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.feature_home.R;
import com.google.android.material.button.MaterialButton;

/**
 * Home screen fragment displaying the main navigation buttons.
 */
public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        MaterialButton buttonPrimary = view.findViewById(R.id.buttonBottomBar);
        MaterialButton buttonSecondary = view.findViewById(R.id.buttonBottomRight);
        MaterialButton buttonTertiary = view.findViewById(R.id.buttonBottomLeft);
        MaterialButton buttonCenter = view.findViewById(R.id.buttonCenter);
        MaterialButton buttonTopLeft = view.findViewById(R.id.buttonTopLeft);
        MaterialButton buttonTopRight = view.findViewById(R.id.buttonTopRight);
        MaterialButton buttonTopRightSecondary = view.findViewById(R.id.buttonTopRightSecondary);

        buttonPrimary.setOnClickListener(v -> viewModel.onPrimaryButtonClicked());
        buttonSecondary.setOnClickListener(v -> viewModel.onSecondaryButtonClicked());
        buttonTertiary.setOnClickListener(v -> viewModel.onTertiaryButtonClicked());
        buttonCenter.setOnClickListener(v -> viewModel.onCenterButtonClicked());
        buttonTopLeft.setOnClickListener(v -> viewModel.onTopLeftButtonClicked());
        buttonTopRight.setOnClickListener(v -> viewModel.onTopRightButtonClicked());
        buttonTopRightSecondary.setOnClickListener(v -> viewModel.onTopRightSecondaryButtonClicked());
    }
}
