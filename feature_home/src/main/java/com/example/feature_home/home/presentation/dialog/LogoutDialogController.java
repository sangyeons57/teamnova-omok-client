package com.example.feature_home.home.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.port.in.UResult;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseRegistry;
import com.example.application.usecase.LogoutUseCase;
import com.example.core.dialog.DialogController;
import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogHostOwner;
import com.example.core.dialog.DialogRequest;
import com.example.core.dialog.MainDialogType;
import com.example.core_di.UseCaseContainer;
import com.example.core_di.sound.SoundEffects;
import com.example.feature_home.R;
import com.example.feature_home.home.presentation.viewmodel.LogoutDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Presents the logout confirmation dialog and triggers the logout use case.
 */
public final class LogoutDialogController implements DialogController<MainDialogType> {

    public static final String ARG_MESSAGE = "message";
    public static final String ARG_TITLE = "title";

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        UseCaseRegistry registry = UseCaseContainer.getInstance().registry;
        LogoutUseCase logoutUseCase = registry.get(LogoutUseCase.class);

        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_logout, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        LogoutDialogViewModel viewModel = new ViewModelProvider(activity).get(LogoutDialogViewModel.class);
        bindViews(activity, contentView, dialog, request.getArguments(), viewModel, logoutUseCase);

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
                           @NonNull Bundle arguments,
                           @NonNull LogoutDialogViewModel viewModel,
                           @NonNull LogoutUseCase logoutUseCase) {
        MaterialTextView titleView = contentView.findViewById(R.id.textLogoutTitle);
        MaterialTextView messageView = contentView.findViewById(R.id.textLogoutMessage);
        MaterialButton closeButton = contentView.findViewById(R.id.buttonLogoutClose);
        MaterialButton confirmButton = contentView.findViewById(R.id.buttonLogoutConfirm);

        CharSequence defaultMessage = activity.getString(R.string.dialog_logout_message_default);
        CharSequence defaultTitle = activity.getString(R.string.dialog_logout_title);
        viewModel.initialize(defaultMessage);

        String messageArg = arguments.getString(ARG_MESSAGE);
        if (messageArg != null && !messageArg.trim().isEmpty()) {
            viewModel.setMessage(messageArg);
        }
        String titleArg = arguments.getString(ARG_TITLE);
        CharSequence title = titleArg != null && !titleArg.trim().isEmpty() ? titleArg : defaultTitle;
        titleView.setText(title);

        viewModel.getMessage().observe(activity, messageView::setText);
        viewModel.isInProgress().observe(activity, inProgress -> {
            boolean enabled = inProgress == null || !inProgress;
            confirmButton.setEnabled(enabled);
            confirmButton.setAlpha(enabled ? 1f : 0.6f);
        });

        closeButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onCloseClicked();
            dialog.dismiss();
        });
        confirmButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            dismissAll(activity);
            handleLogout(activity, dialog, viewModel, logoutUseCase);
        });
    }

    private void handleLogout(@NonNull FragmentActivity activity,
                              @NonNull AlertDialog dialog,
                              @NonNull LogoutDialogViewModel viewModel,
                              @NonNull LogoutUseCase logoutUseCase) {
        if (Boolean.TRUE.equals(viewModel.isInProgress().getValue())) {
            return;
        }

        viewModel.setInProgress(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        logoutUseCase.executeAsync(UseCase.None.INSTANCE, executor)
                .whenComplete((result, throwable) -> {
                    executor.shutdown();
                    activity.runOnUiThread(() -> {
                        viewModel.setInProgress(false);

                        if (throwable != null) {
                            Toast.makeText(activity, R.string.dialog_logout_error_generic, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (result instanceof UResult.Ok<?>) {
                            viewModel.onActionCompleted();
                            dialog.dismiss();
                        } else if (result instanceof UResult.Err<?> err) {
                            String message = err.message();
                            if (message == null || message.trim().isEmpty()) {
                                message = activity.getString(R.string.dialog_logout_error_generic);
                            }
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, R.string.dialog_logout_error_generic, Toast.LENGTH_SHORT).show();
                        }
                    });
                });
    }


    /** @noinspection unchecked*/
    public void dismissAll(@NonNull FragmentActivity activity ) {
        if (!(activity instanceof DialogHostOwner<?> owner)) {
            Log.d("DeleteAccountDialogController", "Activity does not implement DialogHostOwner");
            return;
        }
        Log.d("DeleteAccountDialogController", "Dismissing all dialogs");
        DialogHost<MainDialogType> host = ((DialogHostOwner<MainDialogType>) owner).getDialogHost();
        host.dismissAll();
    }
}
