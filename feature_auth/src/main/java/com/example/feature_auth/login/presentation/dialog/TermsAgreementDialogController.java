package com.example.feature_auth.login.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.example.application.dto.command.AcceptTermsCommand;
import com.example.application.port.in.UResult;
import com.example.application.port.in.UResults;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseRegistry;
import com.example.application.usecase.AllTermsAcceptancesUseCase;
import com.example.application.usecase.LoginUseCase;
import com.example.application.usecase.TcpAuthUseCase;
import com.example.core_api.dialog.DialogArgumentKeys;
import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogHost;
import com.example.core_api.dialog.DialogHostOwner;
import com.example.core_api.dialog.DialogRequest;
import com.example.core_api.dialog.GeneralInfoContentType;
import com.example.core_api.dialog.MainDialogType;
import com.example.core_api.navigation.AppNavigationKey;
import com.example.core_api.navigation.FragmentNavigationHost;
import com.example.core_api.navigation.FragmentNavigationHostOwner;
import com.example.core_api.token.TokenStore;
import com.example.core_di.TokenContainer;
import com.example.core_di.UseCaseContainer;
import com.example.feature_auth.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TermsAgreementDialogController implements DialogController<MainDialogType> {

    private static final String LOG_TAG = "TermsAgreementDlg";

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        UseCaseRegistry registry = UseCaseContainer.getInstance().registry;

        AllTermsAcceptancesUseCase allTermsAcceptancesUseCase = registry.get(AllTermsAcceptancesUseCase.class);
        LoginUseCase loginUseCase = registry.get(LoginUseCase.class);
        TcpAuthUseCase tcpAuthUseCase = registry.get(TcpAuthUseCase.class);
        TokenStore tokenManager = TokenContainer.getInstance();
        //noinspection unchecked
        FragmentNavigationHost<AppNavigationKey> host = ((FragmentNavigationHostOwner<AppNavigationKey>)activity).getFragmentNavigatorHost();

        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_terms_agreement, null, false);

        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        MaterialButton buttonTermsBack = contentView.findViewById(R.id.buttonTermsBack);
        MaterialButton buttonConfirm = contentView.findViewById(R.id.buttonConfirm);
        MaterialTextView privacyPolicyLink = contentView.findViewById(R.id.textPrivacyPolicy);
        MaterialTextView termsOfServiceLink = contentView.findViewById(R.id.textTermsOfService);
        MaterialCheckBox checkPrivacyPolicy = contentView.findViewById(R.id.checkPrivacyPolicy);
        MaterialCheckBox checkTermsOfService = contentView.findViewById(R.id.checkTermsOfService);
        MaterialCheckBox checkAgeConfirmation = contentView.findViewById(R.id.checkAgeConfirmation);

        buttonConfirm.setEnabled(false);

        buttonTermsBack.setOnClickListener(v -> {
            android.util.Log.d(LOG_TAG, "Back button clicked");
            dialog.dismiss();
        });
        privacyPolicyLink.setOnClickListener(v -> {
            android.util.Log.d(LOG_TAG, "Privacy policy link clicked");
            showGeneralInfoDialog(activity, GeneralInfoContentType.PRIVACY_POLICY);
        });
        termsOfServiceLink.setOnClickListener(v -> {
            android.util.Log.d(LOG_TAG, "Terms of service link clicked");
            showGeneralInfoDialog(activity, GeneralInfoContentType.TERMS_OF_SERVICE);
        });

        CompoundButton.OnCheckedChangeListener checkboxListener = (buttonView, isChecked) ->
                updateConfirmState(buttonConfirm, checkPrivacyPolicy, checkTermsOfService, checkAgeConfirmation);
        checkPrivacyPolicy.setOnCheckedChangeListener(checkboxListener);
        checkTermsOfService.setOnCheckedChangeListener(checkboxListener);
        checkAgeConfirmation.setOnCheckedChangeListener(checkboxListener);

        buttonConfirm.setOnClickListener(v -> {
            android.util.Log.d(LOG_TAG, "Confirm button clicked");
            handleConfirmClicked(activity, dialog, buttonConfirm, allTermsAcceptancesUseCase, loginUseCase, tcpAuthUseCase, tokenManager, host);
        });

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }


    private void updateConfirmState(@NonNull MaterialButton buttonConfirm,
                                    @NonNull MaterialCheckBox checkPrivacyPolicy,
                                    @NonNull MaterialCheckBox checkTermsOfService,
                                    @NonNull MaterialCheckBox checkAgeConfirmation) {
        boolean allChecked = checkPrivacyPolicy.isChecked()
                && checkTermsOfService.isChecked()
                && checkAgeConfirmation.isChecked();
        buttonConfirm.setEnabled(allChecked);
    }

    private void handleConfirmClicked(@NonNull FragmentActivity activity,
                                      @NonNull AlertDialog dialog,
                                      @NonNull MaterialButton buttonConfirm,
                                      @NonNull AllTermsAcceptancesUseCase allTermsAcceptancesUseCase,
                                      @NonNull LoginUseCase loginUseCase,
                                      @NonNull TcpAuthUseCase tcpAuthUseCase,
                                      @NonNull TokenStore tokenManager,
                                      FragmentNavigationHost<AppNavigationKey> host
    ) {
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.trim().isEmpty()) {
            android.util.Log.w(LOG_TAG, "Access token is missing");
            Toast.makeText(activity, R.string.error_terms_missing_token, Toast.LENGTH_SHORT).show();
            return;
        }

        AcceptTermsCommand command = AcceptTermsCommand.acceptAll(accessToken);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        buttonConfirm.setEnabled(false);
        allTermsAcceptancesUseCase.executeAsync(command, executorService)
                .thenCompose(res -> {
                    if(UResults.isSuccess(res)) {
                        return loginUseCase.executeAsync(UseCase.None.INSTANCE, executorService);
                    } else {
                        return CompletableFuture.completedFuture(UResults.err( UResults.getErrMessage(res), UResults.getErrCode(res)));
                    }
                }).whenComplete((result, throwable) -> {
                    if(result instanceof UResult.Ok<?> data) {
                        android.util.Log.d(LOG_TAG, "LoginUseCase.executeAsync: " + data.value().toString());
                        dialog.dismiss();
                        host.clearBackStack();
                        host.navigateTo(AppNavigationKey.HOME, false);
                        triggerTcpAuthHandshake(tcpAuthUseCase, tokenManager.getAccessToken());
                    } else if (result instanceof UResult.Err<?> err) {
                        android.util.Log.d(LOG_TAG, "LoginUseCase.executeAsync: " + err.message());
                        buttonConfirm.setEnabled(true);
                        String message = err.message();
                        if (message == null || message.trim().isEmpty()) {
                            message = dialog.getContext().getString(R.string.error_terms_acceptance_failed);
                        }
                        Toast.makeText(dialog.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void triggerTcpAuthHandshake(@NonNull TcpAuthUseCase tcpAuthUseCase, String accessToken) {
        UResult<UseCase.None> result = tcpAuthUseCase.execute(accessToken);
        if (result instanceof UResult.Ok<UseCase.None>) {
            android.util.Log.d(LOG_TAG, "AUTH handshake dispatched");
        } else if (result instanceof UResult.Err<?> err) {
            android.util.Log.e(LOG_TAG, "TcpAuthUseCase execution error: " + err.code() + ", " + err.message());
        }
    }

    private void showGeneralInfoDialog(@NonNull FragmentActivity activity,
                                       @NonNull GeneralInfoContentType type) {
        if (!(activity instanceof DialogHostOwner<?> owner)) {
            return;
        }
        //noinspection unchecked
        DialogHost<MainDialogType> host = ((DialogHostOwner<MainDialogType>) owner).getDialogHost();
        if (!host.isAttached()) {
            return;
        }
        Bundle arguments = new Bundle();
        arguments.putString(DialogArgumentKeys.GENERAL_INFO_TYPE, type.name());
        host.enqueue(MainDialogType.GENERAL_INFO, arguments);
    }
}
