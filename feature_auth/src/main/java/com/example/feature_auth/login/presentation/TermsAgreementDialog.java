package com.example.feature_auth.login.presentation;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.feature_auth.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Dialog presenting the terms agreement list shown after account creation.
 */
public class TermsAgreementDialog extends DialogFragment {

    public static final String TAG = "TermsAgreementDialog";
    private static final String LOG_TAG = "TermsAgreementDlg";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View contentView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_terms_agreement, null, false);

        bindButtons(contentView);
        bindCheckboxes(contentView);

        Dialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void bindButtons(@NonNull View contentView) {
        MaterialButton buttonTabOne = contentView.findViewById(R.id.buttonTabOne);
        MaterialButton buttonTabTwo = contentView.findViewById(R.id.buttonTabTwo);
        MaterialButton buttonTabThree = contentView.findViewById(R.id.buttonTabThree);
        MaterialButton buttonPrimaryTerm = contentView.findViewById(R.id.buttonPrimaryTerm);
        MaterialButton buttonSecondaryTerm = contentView.findViewById(R.id.buttonSecondaryTerm);
        MaterialButton buttonTertiaryTerm = contentView.findViewById(R.id.buttonTertiaryTerm);
        MaterialButton buttonConfirm = contentView.findViewById(R.id.buttonConfirm);

        buttonTabOne.setOnClickListener(v -> Log.d(LOG_TAG, "Tab 1 clicked"));
        buttonTabTwo.setOnClickListener(v -> Log.d(LOG_TAG, "Tab 2 clicked"));
        buttonTabThree.setOnClickListener(v -> Log.d(LOG_TAG, "Tab 3 clicked"));
        buttonPrimaryTerm.setOnClickListener(v -> Log.d(LOG_TAG, "Primary term button clicked"));
        buttonSecondaryTerm.setOnClickListener(v -> Log.d(LOG_TAG, "Secondary term button clicked"));
        buttonTertiaryTerm.setOnClickListener(v -> Log.d(LOG_TAG, "Tertiary term button clicked"));
        buttonConfirm.setOnClickListener(v -> {
            Log.d(LOG_TAG, "Confirm button clicked");
            dismissAllowingStateLoss();
        });
    }

    private void bindCheckboxes(@NonNull View contentView) {
        CheckBox checkPrimaryTerm = contentView.findViewById(R.id.checkPrimaryTerm);
        CheckBox checkSecondaryTerm = contentView.findViewById(R.id.checkSecondaryTerm);
        CheckBox checkTertiaryTerm = contentView.findViewById(R.id.checkTertiaryTerm);

        checkPrimaryTerm.setOnCheckedChangeListener((buttonView, isChecked) ->
                Log.d(LOG_TAG, "Primary term checkbox " + (isChecked ? "checked" : "unchecked")));
        checkSecondaryTerm.setOnCheckedChangeListener((buttonView, isChecked) ->
                Log.d(LOG_TAG, "Secondary term checkbox " + (isChecked ? "checked" : "unchecked")));
        checkTertiaryTerm.setOnCheckedChangeListener((buttonView, isChecked) ->
                Log.d(LOG_TAG, "Tertiary term checkbox " + (isChecked ? "checked" : "unchecked")));
    }
}
