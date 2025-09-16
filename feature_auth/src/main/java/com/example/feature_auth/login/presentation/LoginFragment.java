package com.example.feature_auth.login.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.feature_auth.R;
import com.example.domain.auth.model.LoginAction;
import com.google.android.material.button.MaterialButton;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        MaterialButton guestButton = view.findViewById(R.id.buttonGuestLogin);
        MaterialButton googleButton = view.findViewById(R.id.buttonGoogleLogin);

        guestButton.setOnClickListener(v -> viewModel.onGuestLoginClicked());
        googleButton.setOnClickListener(v -> viewModel.onGoogleLoginClicked());

        viewModel.getLoginAction().observe(getViewLifecycleOwner(), action -> {
            if (action == null) {
                return;
            }

            int messageRes = action == LoginAction.GUEST
                    ? R.string.login_guest_feedback
                    : R.string.login_google_feedback;
            Toast.makeText(requireContext(), messageRes, Toast.LENGTH_SHORT).show();
            viewModel.onActionHandled();
        });
    }
}
