package com.example.feature_auth.login.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogRequest;
import com.example.core.dialog.MainDialogType;
import com.example.feature_auth.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

public final class TermsAgreementDialogController implements DialogController<MainDialogType> {

    private static final String LOG_TAG = "TermsAgreementDlg";

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_terms_agreement, null, false);

        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        bindButtons(contentView, dialog);
        bindCheckboxes(contentView);
        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    private void bindButtons(@NonNull View contentView, @NonNull AlertDialog dialog) {
        MaterialButton buttonTermsBack = contentView.findViewById(R.id.buttonTermsBack);
        MaterialButton buttonConfirm = contentView.findViewById(R.id.buttonConfirm);
        MaterialTextView privacyPolicyLink = contentView.findViewById(R.id.textPrivacyPolicy);
        MaterialTextView termsOfServiceLink = contentView.findViewById(R.id.textTermsOfService);

        buttonTermsBack.setOnClickListener(v -> {
            android.util.Log.d(LOG_TAG, "Back button clicked");
            dialog.dismiss();
        });
        privacyPolicyLink.setOnClickListener(v ->
                android.util.Log.d(LOG_TAG, "Privacy policy link clicked"));
        termsOfServiceLink.setOnClickListener(v ->
                android.util.Log.d(LOG_TAG, "Terms of service link clicked"));
        buttonConfirm.setOnClickListener(v -> {
            android.util.Log.d(LOG_TAG, "Confirm button clicked");
            dialog.dismiss();
        });
    }

    private void bindCheckboxes(@NonNull View contentView) {
        MaterialCheckBox checkPrivacyPolicy = contentView.findViewById(R.id.checkPrivacyPolicy);
        MaterialCheckBox checkTermsOfService = contentView.findViewById(R.id.checkTermsOfService);
        MaterialCheckBox checkAgeConfirmation = contentView.findViewById(R.id.checkAgeConfirmation);

        checkPrivacyPolicy.setOnCheckedChangeListener((buttonView, isChecked) ->
                android.util.Log.d(LOG_TAG, "Privacy policy checkbox " + (isChecked ? "checked" : "unchecked")));
        checkTermsOfService.setOnCheckedChangeListener((buttonView, isChecked) ->
                android.util.Log.d(LOG_TAG, "Terms of service checkbox " + (isChecked ? "checked" : "unchecked")));
        checkAgeConfirmation.setOnCheckedChangeListener((buttonView, isChecked) ->
                android.util.Log.d(LOG_TAG, "Age confirmation checkbox " + (isChecked ? "checked" : "unchecked")));
    }
}
