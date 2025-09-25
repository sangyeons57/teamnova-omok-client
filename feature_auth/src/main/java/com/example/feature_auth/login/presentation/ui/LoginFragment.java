package com.example.feature_auth.login.presentation.ui;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogHostOwner;
import com.example.core.dialog.MainDialogType;
import com.example.core.navigation.AppNavigationKey;
import com.example.core.navigation.FragmentNavigationHost;
import com.example.core.navigation.FragmentNavigationHostOwner;
import com.example.core_di.TokenContainer;
import com.example.domain.common.value.LoginAction;
import com.example.feature_auth.R;
import com.example.feature_auth.login.di.LoginViewModelFactory;
import com.example.feature_auth.login.presentation.viewmodel.LoginViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;
    private DialogHost<MainDialogType> dialogHost;


    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DialogHostOwner<?>) {
            dialogHost = ((DialogHostOwner<MainDialogType>) context).getDialogHost();
        } else {
            throw new IllegalStateException("Host activity must implement MainDialogHostOwner");
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
        super.onDetach();
    }

    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //noinspection unchecked
        LoginViewModelFactory factory = LoginViewModelFactory.create(
                (FragmentNavigationHostOwner<AppNavigationKey>)getActivity()
        );
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
        signInWithGoogle();
    }

    private void showTermsAgreementDialog() {
        if (dialogHost != null && dialogHost.isAttached()) {
            dialogHost.enqueue(MainDialogType.TERMS_AGREEMENT);
        }
    }
    final String WEB_CLIENT_ID = "1043521041767-naub8m6pi6vb0kspnqm4to9ns2js7696.apps.googleusercontent.com";


    @RequiresApi(api = Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    public void signInWithGoogle() {
        GetGoogleIdOption google = new GetGoogleIdOption.Builder()
                .setServerClientId(WEB_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(google)
                .build();
        Executor executor = Executors.newSingleThreadExecutor();
        CancellationSignal cancellationSignal = new CancellationSignal();
        CredentialManager cm = CredentialManager.create(requireContext());
        cm.getCredentialAsync(
                requireActivity(),
                request,
                cancellationSignal,
                executor,
                new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        Log.e("LoginFragment", "Exception", e);
                    }

                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        try {
                            Credential credential = getCredentialResponse.getCredential();
                            if(credential instanceof CustomCredential
                            && WEB_CLIENT_ID.equals(credential.getType())) {
                                Bundle data = ((CustomCredential) credential).getData();
                                GoogleIdTokenCredential gid = GoogleIdTokenCredential.createFrom(data);

                                String idToken = gid.getIdToken();
                                Log.d("LoginFragment", "idToken:" + idToken);
                            } else {
                                Log.e("LoginFragment", "credential is not a CustomCredential");
                            }
                        } catch (Exception e) {
                            Log.e("LoginFragment", "Exception", e);
                        }
                    }

                }
        );
    }
}
