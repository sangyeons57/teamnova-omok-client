package com.example.feature_home.presentation.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.dto.command.ChangeNameCommand;
import com.example.application.dto.command.ChangeProfileIconCommand;
import com.example.application.port.in.UResult;
import com.example.application.port.in.UseCaseRegistry;
import com.example.application.session.UserSessionStore;
import com.example.application.usecase.ChangeNameUseCase;
import com.example.application.usecase.ChangeProfileIconUseCase;
import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogRequest;
import com.example.core_api.dialog.MainDialogType;
import com.example.core_di.UseCaseContainer;
import com.example.core_di.sound.SoundEffects;
import com.example.domain.user.entity.User;
import com.example.domain.user.value.UserDisplayName;
import com.example.domain.user.value.UserProfileIcon;
import com.example.feature_home.R;
import com.example.feature_home.presentation.adapter.ProfileIconAdapter;
import com.example.feature_home.presentation.viewmodel.SettingProfileDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller presenting the profile nickname editor dialog.
 */
public final class SettingProfileDialogController implements DialogController<MainDialogType> {

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        UseCaseRegistry registry = UseCaseContainer.getInstance().registry;
        ChangeNameUseCase changeNameUseCase = registry.get(ChangeNameUseCase.class);
        ChangeProfileIconUseCase changeProfileIconUseCase = registry.get(ChangeProfileIconUseCase.class);
        UserSessionStore sessionStore = UseCaseContainer.getInstance().userSessionStore;

        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_setting_profile, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        SettingProfileDialogViewModel viewModel = new ViewModelProvider(activity).get(SettingProfileDialogViewModel.class);
        applySessionUser(viewModel, sessionStore.getCurrentUser());
        sessionStore.getUserStream().observe(activity, user -> applySessionUser(viewModel, user));
        bindViews(activity, contentView, dialog, viewModel, changeNameUseCase, changeProfileIconUseCase);

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
                           @NonNull SettingProfileDialogViewModel viewModel,
                           @NonNull ChangeNameUseCase changeNameUseCase,
                           @NonNull ChangeProfileIconUseCase changeProfileIconUseCase) {
        MaterialButton close = contentView.findViewById(R.id.buttonProfileClose);
        MaterialButton apply = contentView.findViewById(R.id.buttonProfileApply);
        TextInputEditText input = contentView.findViewById(R.id.inputNickname);
        MaterialTextView statusText = contentView.findViewById(R.id.textNicknameStatus);
        RecyclerView profileIcons = contentView.findViewById(R.id.recyclerProfileIcons);

        GridLayoutManager layoutManager = new GridLayoutManager(activity, 5);
        profileIcons.setLayoutManager(layoutManager);
        profileIcons.setItemAnimator(null);
        profileIcons.setHasFixedSize(true);
        profileIcons.setNestedScrollingEnabled(false);

        ProfileIconAdapter iconAdapter = new ProfileIconAdapter(iconCode -> {
            if (Boolean.TRUE.equals(viewModel.isInProgress().getValue())) {
                return;
            }

            Integer previousSelection = viewModel.getSelectedIcon().getValue();
            viewModel.onIconSelected(iconCode);
            viewModel.clearStatus();
            viewModel.setInProgress(true);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            changeProfileIconUseCase.executeAsync(new ChangeProfileIconCommand(iconCode), executor)
                    .whenComplete((result, throwable) -> {
                        executor.shutdown();
                        activity.runOnUiThread(() -> {
                            viewModel.setInProgress(false);

                            if (throwable != null) {
                                revertIconSelection(viewModel, previousSelection);
                                viewModel.showError(activity.getString(R.string.dialog_setting_profile_icon_change_error));
                                return;
                            }

                            if (result instanceof UResult.Ok<?>) {
                                viewModel.showSuccess(activity.getString(R.string.dialog_setting_profile_icon_change_success));
                            } else if (result instanceof UResult.Err<?> err) {
                                String message = err.message();
                                if (message == null || message.trim().isEmpty()) {
                                    message = activity.getString(R.string.dialog_setting_profile_icon_change_error);
                                }
                                revertIconSelection(viewModel, previousSelection);
                                viewModel.showError(message);
                            } else {
                                revertIconSelection(viewModel, previousSelection);
                                viewModel.showError(activity.getString(R.string.dialog_setting_profile_icon_change_error));
                            }
                        });
                    });
        });
        profileIcons.setAdapter(iconAdapter);

        viewModel.getSelectedIcon().observe(activity, iconCode -> iconAdapter.setSelectedIcon(iconCode));

        viewModel.getNickname().observe(activity, nickname -> {
            if (nickname != null && (input.getText() == null || !nickname.contentEquals(input.getText()))) {
                input.setText(nickname);
                input.setSelection(input.getText() != null ? input.getText().length() : 0);
            }
        });

        viewModel.isInProgress().observe(activity, inProgress -> {
            boolean disabled = Boolean.TRUE.equals(inProgress);
            apply.setEnabled(!disabled);
            apply.setAlpha(disabled ? 0.6f : 1f);
        });

        viewModel.getStatus().observe(activity, status -> {
            if (status == null || status.message == null || status.message.trim().isEmpty()) {
                statusText.setVisibility(View.GONE);
                return;
            }
            statusText.setVisibility(View.VISIBLE);
            statusText.setText(status.message);
            int colorRes = status.isSuccess
                    ? com.example.designsystem.R.color.md_theme_light_primary
                    : com.example.designsystem.R.color.md_theme_light_error;
            statusText.setTextColor(ContextCompat.getColor(statusText.getContext(), colorRes));
        });

        close.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onCloseClicked();
            dialog.dismiss();
        });
        apply.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            CharSequence value = input.getText();
            String nickname = value == null ? "" : value.toString().trim();
            handleApply(activity, viewModel, changeNameUseCase, nickname, input);
        });
    }

    private void applySessionUser(@NonNull SettingProfileDialogViewModel viewModel, @Nullable User user) {
        if (user == null) {
            viewModel.onNicknameChanged("");
            viewModel.clearIconSelection();
            return;
        }
        Log.d("SettingProfileDialog", user.getUserId().getValue() + " " + user.getDisplayName().getValue() + " " + user.getProfileIcon().getValue());

        String displayName = user.getDisplayName() != null ? user.getDisplayName().getValue() : "";
        if (UserDisplayName.EMPTY.getValue().equals(displayName)) {
            displayName = "";
        }
        viewModel.onNicknameChanged(displayName);

        int iconCode = user.getProfileIcon() != null ? user.getProfileIcon().getValue() : UserProfileIcon.EMPTY.getValue();
        if (iconCode >= 0) {
            viewModel.onIconSelected(iconCode);
        } else {
            viewModel.clearIconSelection();
        }
    }

    private void revertIconSelection(@NonNull SettingProfileDialogViewModel viewModel,
                                     @Nullable Integer previousSelection) {
        if (previousSelection == null) {
            viewModel.clearIconSelection();
        } else {
            viewModel.onIconSelected(previousSelection);
        }
    }

    private void handleApply(@NonNull FragmentActivity activity,
                             @NonNull SettingProfileDialogViewModel viewModel,
                             @NonNull ChangeNameUseCase changeNameUseCase,
                             @NonNull String nickname,
                             @NonNull TextInputEditText input) {
        if (Boolean.TRUE.equals(viewModel.isInProgress().getValue())) {
            return;
        }

        if (nickname.isEmpty()) {
            viewModel.showError(activity.getString(R.string.dialog_setting_profile_error_empty));
            return;
        }

        viewModel.clearStatus();
        viewModel.setInProgress(true);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        changeNameUseCase.executeAsync(new ChangeNameCommand(nickname), executor)
                .whenComplete((result, throwable) -> {
                    executor.shutdown();
                    activity.runOnUiThread(() -> {
                        viewModel.setInProgress(false);

                        if (throwable != null) {
                            viewModel.showError(activity.getString(R.string.dialog_setting_profile_error_generic));
                            return;
                        }

                        if (result instanceof UResult.Ok<?>) {
                            viewModel.onNicknameChanged(nickname);
                            viewModel.showSuccess(activity.getString(R.string.dialog_setting_profile_change_success));
                            input.setText(nickname);
                            input.setSelection(nickname.length());
                        } else if (result instanceof UResult.Err<?> err) {
                            String message = err.message();
                            if (message == null || message.trim().isEmpty()) {
                                message = activity.getString(R.string.dialog_setting_profile_error_generic);
                            }
                            viewModel.showError(message);
                        } else {
                            viewModel.showError(activity.getString(R.string.dialog_setting_profile_error_generic));
                        }
                    });
                });
    }
}
