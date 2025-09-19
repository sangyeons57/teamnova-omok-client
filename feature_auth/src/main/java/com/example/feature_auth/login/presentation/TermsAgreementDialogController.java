package com.example.feature_auth.login.presentation;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogRequest;
import com.example.core.dialog.MainDialogType;
import com.example.feature_auth.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

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
        MaterialButton buttonTabOne = contentView.findViewById(R.id.buttonTabOne);
        MaterialButton buttonTabTwo = contentView.findViewById(R.id.buttonTabTwo);
        MaterialButton buttonTabThree = contentView.findViewById(R.id.buttonTabThree);
        MaterialButton buttonPrimaryTerm = contentView.findViewById(R.id.buttonPrimaryTerm);
        MaterialButton buttonSecondaryTerm = contentView.findViewById(R.id.buttonSecondaryTerm);
        MaterialButton buttonTertiaryTerm = contentView.findViewById(R.id.buttonTertiaryTerm);
        MaterialButton buttonConfirm = contentView.findViewById(R.id.buttonConfirm);

        buttonTabOne.setOnClickListener(v -> android.util.Log.d(LOG_TAG, "Tab 1 clicked"));
        buttonTabTwo.setOnClickListener(v -> android.util.Log.d(LOG_TAG, "Tab 2 clicked"));
        buttonTabThree.setOnClickListener(v -> android.util.Log.d(LOG_TAG, "Tab 3 clicked"));
        buttonPrimaryTerm.setOnClickListener(v -> android.util.Log.d(LOG_TAG, "Primary term button clicked"));
        buttonSecondaryTerm.setOnClickListener(v -> android.util.Log.d(LOG_TAG, "Secondary term button clicked"));
        buttonTertiaryTerm.setOnClickListener(v -> android.util.Log.d(LOG_TAG, "Tertiary term button clicked"));
        buttonConfirm.setOnClickListener(v -> {
            android.util.Log.d(LOG_TAG, "Confirm button clicked");
            dialog.dismiss();
        });
    }

    private void bindCheckboxes(@NonNull View contentView) {
        CheckBox checkPrimaryTerm = contentView.findViewById(R.id.checkPrimaryTerm);
        CheckBox checkSecondaryTerm = contentView.findViewById(R.id.checkSecondaryTerm);
        CheckBox checkTertiaryTerm = contentView.findViewById(R.id.checkTertiaryTerm);

        checkPrimaryTerm.setOnCheckedChangeListener((buttonView, isChecked) ->
                android.util.Log.d(LOG_TAG, "Primary term checkbox " + (isChecked ? "checked" : "unchecked")));
        checkSecondaryTerm.setOnCheckedChangeListener((buttonView, isChecked) ->
                android.util.Log.d(LOG_TAG, "Secondary term checkbox " + (isChecked ? "checked" : "unchecked")));
        checkTertiaryTerm.setOnCheckedChangeListener((buttonView, isChecked) ->
                android.util.Log.d(LOG_TAG, "Tertiary term checkbox " + (isChecked ? "checked" : "unchecked")));
    }
}
