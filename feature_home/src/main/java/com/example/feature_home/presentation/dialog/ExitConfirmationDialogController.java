package com.example.feature_home.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogRequest;
import com.example.core_api.dialog.MainDialogType;
import com.example.core_di.sound.SoundEffects;
import com.example.feature_home.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Presents an exit confirmation dialog before closing the app.
 */
public final class ExitConfirmationDialogController implements DialogController<MainDialogType> {

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_exit_confirmation, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();

        dialog.setCanceledOnTouchOutside(true);

        MaterialButton confirmButton = contentView.findViewById(R.id.buttonExitConfirm);

        confirmButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            dialog.dismiss();
            activity.finish();
        });

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });

        return dialog;
    }
}
