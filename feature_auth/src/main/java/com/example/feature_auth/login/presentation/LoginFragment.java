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

import com.example.core.dialog.DialogManager;
import com.example.core.dialog.DialogRegistry;
import com.example.domain.auth.model.LoginAction;
import com.example.feature_auth.R;
import com.google.android.material.button.MaterialButton;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;
    private DialogManager<AuthDialogType> dialogManager;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        DialogRegistry<AuthDialogType> registry = new DialogRegistry<>(AuthDialogType.class);
        registry.register(AuthDialogType.TERMS_AGREEMENT, new TermsAgreementDialogController());
        dialogManager = new DialogManager<>(registry);
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

        if (dialogManager != null && !dialogManager.isAttached()) {
            FragmentActivity activity = requireActivity();
            dialogManager.attach(activity);
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

    @Override
    public void onDestroyView() {
        if (dialogManager != null && dialogManager.isAttached()) {
            dialogManager.detach(requireActivity());
        }
        super.onDestroyView();
    }

    private void showTermsAgreementDialog() {
        if (dialogManager != null && dialogManager.isAttached()) {
            dialogManager.enqueue(AuthDialogType.TERMS_AGREEMENT);
        }
    }
}
