package com.example.feature_game.game.presentation.dialog;

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
import com.example.feature_game.R;
import com.example.feature_game.game.di.GameInfoDialogViewModelFactory;
import com.example.feature_game.game.presentation.viewmodel.GameInfoDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Shows a translucent overlay with quick game actions.
 */
public final class GameInfoDialogController implements DialogController<MainDialogType> {

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_game_info, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .setCancelable(true)
                .create();

        GameInfoDialogViewModelFactory factory = GameInfoDialogViewModelFactory.create();
        GameInfoDialogViewModel viewModel = new ViewModelProvider(activity, factory)
                .get(GameInfoDialogViewModel.class);
        bind(contentView, dialog, viewModel, activity);

        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    private void bind(@NonNull View root,
                      @NonNull AlertDialog dialog,
                      @NonNull GameInfoDialogViewModel viewModel,
                      @NonNull FragmentActivity activity) {
        MaterialButton closeButton = root.findViewById(R.id.buttonCloseInfo);

        viewModel.getDismissEvent().observe(activity, dismiss -> {
            if (dismiss == null || !dismiss) {
                return;
            }
            dialog.dismiss();
            viewModel.onEventHandled();
        });

        closeButton.setOnClickListener(v -> viewModel.onCloseClicked());
    }
}
