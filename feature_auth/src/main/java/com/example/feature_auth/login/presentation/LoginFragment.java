package com.example.feature_auth.login.presentation;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.core.dialog.DialogHost;
import com.example.domain.auth.model.LoginAction;
import com.example.feature_auth.R;
import com.example.feature_auth.login.di.AuthDialogHostOwner;
import com.google.android.material.button.MaterialButton;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;
    private DialogHost<AuthDialogType> dialogHost;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AuthDialogHostOwner) {
            dialogHost = ((AuthDialogHostOwner) context).getDialogHost();
        } else {
            throw new IllegalStateException("Host activity must implement AuthDialogHostOwner");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        if (dialogHost != null && !dialogHost.isAttached()) {
            FragmentActivity activity = requireActivity();
            dialogHost.attach(activity);
        }

        MaterialButton guestButton = view.findViewById(R.id.buttonGuestLogin);
        MaterialButton googleButton = view.findViewById(R.id.buttonGoogleLogin);

        guestButton.setOnClickListener(v -> viewModel.onGuestLoginClicked());
        googleButton.setOnClickListener(v -> viewModel.onGoogleLoginClicked());

        viewModel.getLoginAction().observe(getViewLifecycleOwner(), action -> {
            if (action == null) {
                return;
            }

            if (action == LoginAction.GUEST || action == LoginAction.GOOGLE) {
                showTermsAgreementDialog();
            }
            viewModel.onActionHandled();
        });
    }

    private void showTermsAgreementDialog() {
        if (dialogHost != null && dialogHost.isAttached()) {
            dialogHost.enqueue(AuthDialogType.TERMS_AGREEMENT);
        }
    }
}
