package com.example.feature_home.home.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogRequest;
import com.example.core.dialog.MainDialogType;
import com.example.feature_home.R;
import com.example.feature_home.home.presentation.viewmodel.SettingProfileDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

/**
 * Controller presenting the profile nickname editor dialog.
 */
public final class SettingProfileDialogController implements DialogController<MainDialogType> {

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_setting_profile, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        SettingProfileDialogViewModel viewModel = new ViewModelProvider(activity).get(SettingProfileDialogViewModel.class);
        bindViews(activity, contentView, dialog, viewModel);

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    private void bindViews(@NonNull FragmentActivity activity,
                           @NonNull View contentView,
                           @NonNull AlertDialog dialog,
                           @NonNull SettingProfileDialogViewModel viewModel) {
        MaterialButton close = contentView.findViewById(R.id.buttonProfileClose);
        MaterialButton apply = contentView.findViewById(R.id.buttonProfileApply);
        TextInputEditText input = contentView.findViewById(R.id.inputNickname);


        close.setOnClickListener(v -> {
            viewModel.onCloseClicked();
            dialog.dismiss();
        });
        apply.setOnClickListener(v -> {
            CharSequence value = input.getText();
            viewModel.onNicknameChanged(value == null ? "" : value.toString());
        });
    }
}
