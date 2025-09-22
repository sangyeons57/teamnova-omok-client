package com.example.feature_auth.login.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseRegistryProvider;
import com.example.application.usecase.AllTermsAcceptancesUseCase;
import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogRequest;
import com.example.core.dialog.MainDialogType;
import com.example.core.token.TokenManager;
import com.example.core.token.TokenManagerProvider;
import com.example.feature_auth.R;
import com.example.feature_auth.login.di.TermsAgreementHandler;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class TermsAgreementDialogController implements DialogController<MainDialogType> {

    private static final String LOG_TAG = "TermsAgreementDlg";

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        UseCaseRegistryProvider registryProvider = resolveUseCaseRegistryProvider(activity);
        TokenManagerProvider tokenProvider = resolveTokenManagerProvider(activity);

        AllTermsAcceptancesUseCase useCase = registryProvider.getUseCaseRegistry().get(AllTermsAcceptancesUseCase.class);
        TokenManager tokenManager = tokenProvider.getTokenManager();
        TermsAgreementHandler agreementHandler = activity instanceof TermsAgreementHandler handler ? handler : null;

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
        privacyPolicyLink.setOnClickListener(v ->
                android.util.Log.d(LOG_TAG, "Privacy policy link clicked"));
        termsOfServiceLink.setOnClickListener(v ->
                android.util.Log.d(LOG_TAG, "Terms of service link clicked"));

        CompoundButton.OnCheckedChangeListener checkboxListener = (buttonView, isChecked) ->
                updateConfirmState(buttonConfirm, checkPrivacyPolicy, checkTermsOfService, checkAgeConfirmation);
        checkPrivacyPolicy.setOnCheckedChangeListener(checkboxListener);
        checkTermsOfService.setOnCheckedChangeListener(checkboxListener);
        checkAgeConfirmation.setOnCheckedChangeListener(checkboxListener);

        buttonConfirm.setOnClickListener(v -> {
            android.util.Log.d(LOG_TAG, "Confirm button clicked");
            handleConfirmClicked(activity, dialog, buttonConfirm, useCase, tokenManager, agreementHandler);
        });

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    private UseCaseRegistryProvider resolveUseCaseRegistryProvider(@NonNull FragmentActivity activity) {
        if (activity instanceof UseCaseRegistryProvider provider) {
            return provider;
        }
        throw new IllegalStateException("Host activity must provide UseCaseRegistryProvider");
    }

    private TokenManagerProvider resolveTokenManagerProvider(@NonNull FragmentActivity activity) {
        if (activity instanceof TokenManagerProvider provider) {
            return provider;
        }
        throw new IllegalStateException("Host activity must provide TokenManagerProvider");
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
                                      @NonNull AllTermsAcceptancesUseCase useCase,
                                      @NonNull TokenManager tokenManager,
                                      TermsAgreementHandler agreementHandler) {
        String accessToken = tokenManager.getAccessToken();
        if (accessToken == null || accessToken.trim().isEmpty()) {
            android.util.Log.w(LOG_TAG, "Access token is missing");
            Toast.makeText(activity, R.string.error_terms_missing_token, Toast.LENGTH_SHORT).show();
            return;
        }

        AcceptTermsCommand command = AcceptTermsCommand.acceptAll(accessToken);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        buttonConfirm.setEnabled(false);
        useCase.executeAsync(command, executorService)
                .whenComplete((result, throwable) -> {
                    executorService.shutdown();
                    activity.runOnUiThread(() -> {
                        if (throwable != null) {
                            android.util.Log.e(LOG_TAG, "Failed to accept terms", throwable);
                            buttonConfirm.setEnabled(true);
                            Toast.makeText(activity, R.string.error_terms_acceptance_failed, Toast.LENGTH_SHORT).show();
                        } else {
                            handleResult(dialog, buttonConfirm, result, agreementHandler);
                        }
                    });
                });
    }

    private void handleResult(@NonNull AlertDialog dialog,
                              @NonNull MaterialButton buttonConfirm,
                              @NonNull UResult<UseCase.None> result,
                              TermsAgreementHandler agreementHandler) {
        if (result instanceof UResult.Ok<?>) {
            dialog.dismiss();
            if (agreementHandler != null) {
                agreementHandler.onAllTermsAccepted();
            }
        } else if (result instanceof UResult.Err<?>) {
            UResult.Err<?> err = (UResult.Err<?>) result;
            buttonConfirm.setEnabled(true);
            String message = err.message();
            if (message == null || message.trim().isEmpty()) {
                message = dialog.getContext().getString(R.string.error_terms_acceptance_failed);
            }
            Toast.makeText(dialog.getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
