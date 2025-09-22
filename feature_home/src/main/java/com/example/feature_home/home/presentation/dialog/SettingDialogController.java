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
import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogHostOwner;
import com.example.core.dialog.DialogRequest;
import com.example.core.dialog.MainDialogType;
import com.example.feature_home.R;
import com.example.feature_home.home.presentation.viewmodel.SettingDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Controller responsible for the general settings dialog.
 */
public final class SettingDialogController implements DialogController<MainDialogType> {

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_setting, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        SettingDialogViewModel viewModel = new ViewModelProvider(activity).get(SettingDialogViewModel.class);
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
        MaterialButton sound = contentView.findViewById(R.id.buttonSettingSound);
        MaterialButton notifications = contentView.findViewById(R.id.buttonSettingNotifications);
        MaterialButton graphics = contentView.findViewById(R.id.buttonSettingGraphics);
        MaterialButton profile = contentView.findViewById(R.id.buttonSettingProfile);
        MaterialButton support = contentView.findViewById(R.id.buttonSettingSupport);

        close.setOnClickListener(v -> {
            viewModel.onCloseClicked();
            dialog.dismiss();
        });
        sound.setOnClickListener(v -> viewModel.onGeneralSettingClicked("sound"));
        notifications.setOnClickListener(v -> viewModel.onGeneralSettingClicked("notifications"));
        graphics.setOnClickListener(v -> viewModel.onGeneralSettingClicked("graphics"));
        support.setOnClickListener(v -> viewModel.onGeneralSettingClicked("support"));
        profile.setOnClickListener(v -> {
            viewModel.onOpenProfileClicked();
            if (activity instanceof DialogHostOwner<?>) {
                DialogHost<MainDialogType> host = ((DialogHostOwner<MainDialogType>) activity).getDialogHost();
                if (host.isAttached()) {
                    host.enqueue(MainDialogType.SETTING_PROFILE);
                }
            }
        });
    }
}
