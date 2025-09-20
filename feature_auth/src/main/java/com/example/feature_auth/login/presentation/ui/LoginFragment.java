package com.example.feature_auth.login.presentation.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.example.domain.common.value.LoginAction;
import com.example.feature_auth.R;
import com.example.feature_auth.login.di.LoginDependencyProvider;
import com.example.feature_auth.login.di.LoginViewModelFactory;
import com.example.feature_auth.login.presentation.viewmodel.LoginViewModel;
import com.google.android.material.button.MaterialButton;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;
    private DialogHost<MainDialogType> dialogHost;
    private LoginDependencyProvider dependencyProvider;

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DialogHostOwner<?>) {
            dialogHost = ((DialogHostOwner<MainDialogType>) context).getDialogHost();
        } else {
            throw new IllegalStateException("Host activity must implement MainDialogHostOwner");
        }

        if (context instanceof LoginDependencyProvider provider) {
            dependencyProvider = provider;
        } else {
            throw new IllegalStateException("Host activity must implement LoginDependencyProvider");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onDetach() {
        dialogHost = null;
        dependencyProvider = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LoginViewModelFactory factory = LoginViewModelFactory.create(
                dependencyProvider.getUseCaseRegistry(),
                dependencyProvider.getTokenManager());
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        MaterialButton guestButton = view.findViewById(R.id.buttonGuestLogin);
        MaterialButton googleButton = view.findViewById(R.id.buttonGoogleLogin);

        guestButton.setOnClickListener(v -> viewModel.onGuestLoginClicked());
        googleButton.setOnClickListener(v -> viewModel.onGoogleLoginClicked());

        viewModel.getLoginAction().observe(getViewLifecycleOwner(), action -> {
            Log.d("LoginFragment", "LoginAction:" + action);
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
            dialogHost.enqueue(MainDialogType.TERMS_AGREEMENT);
        }
    }
}
