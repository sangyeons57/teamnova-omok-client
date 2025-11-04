package com.example.feature_home.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.core_api.dialog.DialogArgumentKeys;
import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogHost;
import com.example.core_api.dialog.DialogHostOwner;
import com.example.core_api.dialog.DialogRequest;
import com.example.core_api.dialog.GeneralInfoContentType;
import com.example.core_api.dialog.MainDialogType;
import com.example.domain.common.value.AuthProvider;
import com.example.feature_home.R;
import com.example.feature_home.di.SettingDialogViewModelFactory;
import com.example.feature_home.presentation.viewmodel.SettingDialogViewModel;
import com.example.feature_home.presentation.viewmodel.SettingDialogViewModel.SettingDialogEvent;
import com.example.core_di.sound.SoundEffects;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller responsible for the general settings dialog.
 */
public final class SettingDialogController implements DialogController<MainDialogType> {

    private static final String TAG = "SettingDialogController";
    private static final String WEB_CLIENT_ID = "525482813681-ea5mpfth6hr7bbd7qk2qj10slruclefb.apps.googleusercontent.com";

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_setting, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        SettingDialogViewModelFactory factory = SettingDialogViewModelFactory.create();
        SettingDialogViewModel viewModel = new ViewModelProvider(activity, factory).get(SettingDialogViewModel.class);
        bindButtons(activity, contentView, dialog, viewModel);

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    /** @noinspection unchecked*/
    private void bindButtons(@NonNull FragmentActivity activity,
                             @NonNull View contentView,
                             @NonNull AlertDialog dialog,
                             @NonNull SettingDialogViewModel viewModel) {
        MaterialButton close = contentView.findViewById(R.id.buttonSettingClose);
        MaterialButton google = contentView.findViewById(R.id.buttonSettingGoogle);
        MaterialButton profile = contentView.findViewById(R.id.buttonSettingProfile);
        MaterialButton language = contentView.findViewById(R.id.buttonSettingLanguage);
        MaterialButton privacy = contentView.findViewById(R.id.buttonSettingPrivacy);
        MaterialButton terms = contentView.findViewById(R.id.buttonSettingTerms);
        MaterialButton logout = contentView.findViewById(R.id.buttonSettingLogout);
        MaterialButton withdraw = contentView.findViewById(R.id.buttonSettingWithdraw);
        LinearLayout authButtonsContainer = contentView.findViewById(R.id.containerAuthButtons);

        close.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onCloseClicked();
            dialog.dismiss();
        });
        google.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onGoogleSettingClicked();
        });
        language.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onGeneralSettingClicked("language");
        });
        privacy.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onGeneralSettingClicked("privacy_policy");
            enqueueDialog(activity, MainDialogType.GENERAL_INFO, createGeneralInfoArguments(GeneralInfoContentType.PRIVACY_POLICY));
        });
        terms.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onGeneralSettingClicked("terms_of_service");
            enqueueDialog(activity, MainDialogType.GENERAL_INFO, createGeneralInfoArguments(GeneralInfoContentType.TERMS_OF_SERVICE));
        });
        logout.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onGeneralSettingClicked("logout");
            viewModel.onLogoutRequested();
            enqueueDialog(activity, MainDialogType.LOGOUT_CONFIRMATION, null);
        });
        withdraw.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onGeneralSettingClicked("withdraw");
            viewModel.onWithdrawRequested();
            enqueueDialog(activity, MainDialogType.ACCOUNT_DELETION_CONFIRMATION, null);
        });
        profile.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onGeneralSettingClicked("profile");
            viewModel.onOpenProfileClicked();
            enqueueDialog(activity, MainDialogType.SETTING_PROFILE, null);
        });

        viewModel.isGoogleButtonEnabled().observe(activity, value -> applyGoogleButtonState(activity, google, viewModel));
        viewModel.isGoogleLinkInProgress().observe(activity, value -> applyGoogleButtonState(activity, google, viewModel));
        viewModel.isGoogleLinked().observe(activity, value -> applyGoogleButtonState(activity, google, viewModel));
        viewModel.getEvents().observe(activity, event -> handleEvent(activity, viewModel, event));

        viewModel.getUserSessionStore().getSessionStream().observe(activity, session -> {
            boolean isGuest = session == null || session.getProvider() == AuthProvider.GUEST;
            if (isGuest) {
                logout.setVisibility(View.GONE);
                withdraw.setText(R.string.dialog_setting_delete_local_data);
                withdraw.setIconResource(R.drawable.ic_setting_delete);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) withdraw.getLayoutParams();
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                params.setMarginStart(0);
                withdraw.setLayoutParams(params);
            } else {
                logout.setVisibility(View.VISIBLE);
                withdraw.setText(R.string.dialog_setting_withdraw);
                withdraw.setIconResource(R.drawable.ic_setting_withdraw);

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) withdraw.getLayoutParams();
                params.width = 0;
                params.weight = 1;
                params.setMarginStart(activity.getResources().getDimensionPixelSize(R.dimen.spacing_small));
                withdraw.setLayoutParams(params);

                LinearLayout.LayoutParams logoutParams = (LinearLayout.LayoutParams) logout.getLayoutParams();
                logoutParams.width = 0;
                logoutParams.weight = 1;
                logoutParams.setMarginEnd(activity.getResources().getDimensionPixelSize(R.dimen.spacing_small));
                logout.setLayoutParams(logoutParams);
            }
        });

        applyGoogleButtonState(activity, google, viewModel);
    }

    private void handleEvent(@NonNull FragmentActivity activity,
                             @NonNull SettingDialogViewModel viewModel,
                             @Nullable SettingDialogEvent event) {
        if (event == null) {
            return;
        }

        switch (event.type()) {
            case REQUEST_GOOGLE_SIGN_IN:
                startGoogleSignIn(activity, viewModel);
                break;
            case SHOW_SUCCESS:
            case SHOW_ERROR:
                showToast(activity, event);
                break;
            default:
                break;
        }
        viewModel.onEventHandled();
    }

    private void startGoogleSignIn(@NonNull FragmentActivity activity,
                                   @NonNull SettingDialogViewModel viewModel) {

        GetGoogleIdOption googleOption = new GetGoogleIdOption.Builder()
                .setServerClientId(WEB_CLIENT_ID)
                .setFilterByAuthorizedAccounts(false)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleOption)
                .build();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        CredentialManager credentialManager = CredentialManager.create(activity);
        credentialManager.getCredentialAsync(activity, request, null, executor, getCredentialCallback(executor, viewModel));
    }

    private CredentialManagerCallback<GetCredentialResponse, GetCredentialException> getCredentialCallback(ExecutorService executor, SettingDialogViewModel viewModel) {
        return new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
            @Override
            public void onError(@NonNull GetCredentialException e) {
                executor.shutdown();
                Log.e(TAG, "Google sign-in error", e);
                viewModel.onGoogleSignInFailed(e.getMessage());
            }

            @Override
            public void onResult(GetCredentialResponse response) {
                try {
                    Credential credential = response.getCredential();
                    if (credential instanceof CustomCredential
                            && GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                        GoogleIdTokenCredential gid = GoogleIdTokenCredential.createFrom(((CustomCredential) credential).getData());
                        String googleIdToken = gid.getIdToken();
                        if (googleIdToken == null || googleIdToken.trim().isEmpty()) {
                            googleIdToken = gid.getId();
                        }
                        if (googleIdToken == null || googleIdToken.trim().isEmpty()) {
                            viewModel.onGoogleSignInFailed(null);
                            return;
                        }
                        viewModel.onGoogleCredentialReceived(googleIdToken);
                    } else {
                        viewModel.onGoogleSignInFailed(null);
                    }
                } catch (Exception exception) {
                    Log.e(TAG, "Google sign-in result error", exception);
                    viewModel.onGoogleSignInFailed(exception.getMessage());
                } finally {
                    executor.shutdown();
                }
            }
        };
    }

    private void applyGoogleButtonState(@NonNull FragmentActivity activity,
                                        @NonNull MaterialButton button,
                                        @NonNull SettingDialogViewModel viewModel) {
        boolean enabled = Boolean.TRUE.equals(viewModel.isGoogleButtonEnabled().getValue());
        boolean linked = Boolean.TRUE.equals(viewModel.isGoogleLinked().getValue());
        boolean inProgress = Boolean.TRUE.equals(viewModel.isGoogleLinkInProgress().getValue());

        button.setEnabled(enabled);
        button.setAlpha(enabled ? 1f : 0.6f);

        int textRes;
        if (inProgress) {
            textRes = R.string.dialog_setting_google_linking;
        } else if (linked) {
            textRes = R.string.dialog_setting_google_linked;
        } else {
            textRes = R.string.dialog_setting_google_link;
        }
        button.setText(activity.getString(textRes));
    }

    private void showToast(@NonNull FragmentActivity activity, @NonNull SettingDialogEvent event) {
        String message = event.message();
        if (message == null || message.trim().isEmpty()) {
            message = event.type() == SettingDialogEvent.Type.SHOW_SUCCESS
                    ? activity.getString(R.string.dialog_setting_google_success)
                    : activity.getString(R.string.dialog_setting_google_error_generic);
        }
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    @NonNull
    private Bundle createGeneralInfoArguments(@NonNull GeneralInfoContentType type) {
        Bundle bundle = new Bundle();
        bundle.putString(DialogArgumentKeys.GENERAL_INFO_TYPE, type.name());
        return bundle;
    }

    /** @noinspection unchecked*/
    private void enqueueDialog(@NonNull FragmentActivity activity,
                               @NonNull MainDialogType type,
                               @Nullable Bundle arguments) {
        if (!(activity instanceof DialogHostOwner<?>)) {
            return;
        }
        DialogHostOwner<MainDialogType> owner = (DialogHostOwner<MainDialogType>) activity;
        DialogHost<MainDialogType> host = owner.getDialogHost();
        if (!host.isAttached()) {
            return;
        }
        if (arguments == null) {
            host.enqueue(type);
        } else {
            host.enqueue(type, arguments);
        }
    }
}
