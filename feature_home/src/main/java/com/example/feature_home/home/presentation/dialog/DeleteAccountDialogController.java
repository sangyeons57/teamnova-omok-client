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
import com.example.application.usecase.DeactivateAccountUseCase;
import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogHost;
import com.example.core_api.dialog.DialogHostOwner;
import com.example.core_api.dialog.DialogRequest;
import com.example.core_api.dialog.MainDialogType;
import com.example.core_di.UseCaseContainer;
import com.example.core_di.sound.SoundEffects;
import com.example.feature_home.R;
import com.example.feature_home.home.presentation.viewmodel.DeleteAccountDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Presents the account deletion confirmation dialog and invokes the deactivate account use case.
 */
public final class DeleteAccountDialogController implements DialogController<MainDialogType> {

    public static final String ARG_MESSAGE = "message";
    public static final String ARG_TITLE = "title";

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        UseCaseRegistry registry = UseCaseContainer.getInstance().registry;
        DeactivateAccountUseCase deactivateAccountUseCase = registry.get(DeactivateAccountUseCase.class);

        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_delete_account, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        DeleteAccountDialogViewModel viewModel = new ViewModelProvider(activity).get(DeleteAccountDialogViewModel.class);
        bindViews(activity, contentView, dialog, request.getArguments(), viewModel, deactivateAccountUseCase);

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
                           @NonNull DeleteAccountDialogViewModel viewModel,
                           @NonNull DeactivateAccountUseCase deactivateAccountUseCase) {
        MaterialTextView titleView = contentView.findViewById(R.id.textDeleteAccountTitle);
        MaterialTextView messageView = contentView.findViewById(R.id.textDeleteAccountMessage);
        MaterialButton closeButton = contentView.findViewById(R.id.buttonDeleteAccountClose);
        MaterialButton confirmButton = contentView.findViewById(R.id.buttonDeleteAccountConfirm);
        MaterialCheckBox acknowledgeCheck = contentView.findViewById(R.id.checkDeleteAccountAcknowledge);

        CharSequence defaultMessage = activity.getString(R.string.dialog_delete_account_message_default);
        CharSequence defaultTitle = activity.getString(R.string.dialog_delete_account_title);
        viewModel.initialize(defaultMessage);

        String messageArg = arguments.getString(ARG_MESSAGE);
        if (messageArg != null && !messageArg.trim().isEmpty()) {
            viewModel.setMessage(messageArg);
        }
        String titleArg = arguments.getString(ARG_TITLE);
        CharSequence title = titleArg != null && !titleArg.trim().isEmpty() ? titleArg : defaultTitle;
        titleView.setText(title);

        viewModel.getMessage().observe(activity, messageView::setText);
        viewModel.isAcknowledged().observe(activity, acknowledged -> updateConfirmState(confirmButton, viewModel));
        viewModel.isInProgress().observe(activity, inProgress -> updateConfirmState(confirmButton, viewModel));

        acknowledgeCheck.setChecked(Boolean.TRUE.equals(viewModel.isAcknowledged().getValue()));
        acknowledgeCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SoundEffects.playButtonClick();
            viewModel.setAcknowledged(isChecked);
        });

        closeButton.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onCloseClicked();
            dialog.dismiss();
        });
        confirmButton.setOnClickListener(v ->{
            SoundEffects.playButtonClick();
            dismissAll(activity);
            handleAccountDeletion(activity, dialog, viewModel, deactivateAccountUseCase);
        });

        updateConfirmState(confirmButton, viewModel);
    }

    private void updateConfirmState(@NonNull MaterialButton confirmButton,
                                    @NonNull DeleteAccountDialogViewModel viewModel) {
        boolean acknowledged = Boolean.TRUE.equals(viewModel.isAcknowledged().getValue());
        boolean inProgress = Boolean.TRUE.equals(viewModel.isInProgress().getValue());
        boolean enabled = acknowledged && !inProgress;
        confirmButton.setEnabled(enabled);
        confirmButton.setAlpha(enabled ? 1f : 0.6f);
    }

    private void handleAccountDeletion(@NonNull FragmentActivity activity,
                                       @NonNull AlertDialog dialog,
                                       @NonNull DeleteAccountDialogViewModel viewModel,
                                       @NonNull DeactivateAccountUseCase deactivateAccountUseCase) {
        if (!Boolean.TRUE.equals(viewModel.isAcknowledged().getValue())) {
            Toast.makeText(activity, R.string.dialog_delete_account_acknowledge_hint, Toast.LENGTH_SHORT).show();
            return;
        }
        if (Boolean.TRUE.equals(viewModel.isInProgress().getValue())) {
            return;
        }

        viewModel.setInProgress(true);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        deactivateAccountUseCase.executeAsync(UseCase.None.INSTANCE, executor)
                .whenComplete((result, throwable) -> {
                    executor.shutdown();
                    activity.runOnUiThread(() -> {
                        viewModel.setInProgress(false);

                        if (throwable != null) {
                            Toast.makeText(activity, R.string.dialog_delete_account_error_generic, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (result instanceof UResult.Ok<?>) {
                            viewModel.onActionCompleted();
                            dialog.dismiss();
                        } else if (result instanceof UResult.Err<?> err) {
                            String message = err.message();
                            if (message == null || message.trim().isEmpty()) {
                                message = activity.getString(R.string.dialog_delete_account_error_generic);
                            }
                            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, R.string.dialog_delete_account_error_generic, Toast.LENGTH_SHORT).show();
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
