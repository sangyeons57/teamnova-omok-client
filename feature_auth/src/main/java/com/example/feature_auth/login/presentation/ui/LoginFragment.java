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

import com.example.core_api.dialog.DialogHost;
import com.example.core_api.dialog.DialogHostOwner;
import com.example.core_api.dialog.MainDialogType;
import com.example.core_api.navigation.AppNavigationKey;
import com.example.core_api.navigation.FragmentNavigationHostOwner;
import com.example.domain.common.value.AuthProvider;
import com.example.feature_auth.R;
import com.example.feature_auth.login.di.LoginViewModelFactory;
import com.example.feature_auth.login.presentation.viewmodel.LoginViewModel;
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
        googleButton.setOnClickListener(v -> signInWithGoogle());

        viewModel.getLoginAction().observe(getViewLifecycleOwner(), action -> {
            Log.d("LoginFragment", "LoginAction:" + action);
            if (action == null) {
                return;
            }

            if (action == AuthProvider.GUEST || action == AuthProvider.GOOGLE) {
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
    final String WEB_CLIENT_ID = "525482813681-ea5mpfth6hr7bbd7qk2qj10slruclefb.apps.googleusercontent.com";


    public void signInWithGoogle() {
        GetGoogleIdOption google = new GetGoogleIdOption.Builder()
                .setServerClientId(WEB_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(google)
                .build();
        Executor executor = Executors.newSingleThreadExecutor();
        CancellationSignal cancellationSignal = new CancellationSignal();
        CredentialManager cm = CredentialManager.create(requireContext());
        cm.getCredentialAsync(requireActivity(), request, null, executor, getCredentialCallback());
    }

    public CredentialManagerCallback<GetCredentialResponse, GetCredentialException> getCredentialCallback() {
        return new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
            @Override
            public void onError(@NonNull GetCredentialException e) {
                Log.e("LoginFragment", "[onError] Exception", e );
                viewModel.onGoogleSignInFailed(e.getMessage());
            }

            @Override
            public void onResult(GetCredentialResponse getCredentialResponse) {
                try {
                    Credential credential = getCredentialResponse.getCredential();
                    if (credential instanceof CustomCredential
                            && GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                        Bundle data = ((CustomCredential) credential).getData();
                        GoogleIdTokenCredential gid = GoogleIdTokenCredential.createFrom(data);

                        String googleIdToken = gid.getIdToken();
                        if (googleIdToken == null || googleIdToken.trim().isEmpty()) {
                            googleIdToken = gid.getId();
                        }

                        if (googleIdToken == null || googleIdToken.trim().isEmpty()) {
                            Log.e("LoginFragment", "Google credential missing id token and subject");
                            viewModel.onGoogleSignInFailed("Google 계정을 확인할 수 없습니다.");
                            return;
                        }

                        Log.d("LoginFragment", "googleIdToken:" + googleIdToken);
                        viewModel.onGoogleCredentialReceived(googleIdToken);
                    } else {
                        Log.e("LoginFragment", "credential is not a CustomCredential");
                        viewModel.onGoogleSignInFailed("Google 로그인에 실패했습니다.");
                    }
                } catch (Exception e) {
                    Log.e("LoginFragment", "[onResult] Exception", e);
                    viewModel.onGoogleSignInFailed(e.getMessage());
                }
            }
        };
    }
}
