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
import com.example.feature_home.home.presentation.viewmodel.GameModeDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Dialog controller rendering the game mode chooser.
 */
public final class GameModeDialogController implements DialogController<MainDialogType> {

    @NonNull
    @Override
    public AlertDialog create(@NonNull FragmentActivity activity, @NonNull DialogRequest<MainDialogType> request) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_game_mode, null, false);
        AlertDialog dialog = new MaterialAlertDialogBuilder(activity)
                .setView(contentView)
                .create();
        dialog.setCanceledOnTouchOutside(false);

        GameModeDialogViewModel viewModel = new ViewModelProvider(activity).get(GameModeDialogViewModel.class);
        bindButtons(contentView, dialog, viewModel);
        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    private void bindButtons(@NonNull View contentView,
                             @NonNull AlertDialog dialog,
                             @NonNull GameModeDialogViewModel viewModel) {
        MaterialButton close = contentView.findViewById(R.id.buttonGameModeClose);
        MaterialButton primary = contentView.findViewById(R.id.buttonGameModePrimary);
        MaterialButton ranked = contentView.findViewById(R.id.buttonGameModeRanked);
        MaterialButton casual = contentView.findViewById(R.id.buttonGameModeCasual);
        MaterialButton tutorial = contentView.findViewById(R.id.buttonGameModeTutorial);
        MaterialButton friends = contentView.findViewById(R.id.buttonGameModeFriends);
        MaterialButton eventButton = contentView.findViewById(R.id.buttonGameModeEvent);
        MaterialButton custom = contentView.findViewById(R.id.buttonGameModeCustom);

        close.setOnClickListener(v -> {
            viewModel.onCloseClicked();
            dialog.dismiss();
        });
        primary.setOnClickListener(v -> viewModel.onModeSelected("quick"));
        ranked.setOnClickListener(v -> viewModel.onModeSelected("ranked"));
        casual.setOnClickListener(v -> viewModel.onModeSelected("casual"));
        tutorial.setOnClickListener(v -> viewModel.onModeSelected("tutorial"));
        friends.setOnClickListener(v -> viewModel.onModeSelected("friends"));
        eventButton.setOnClickListener(v -> viewModel.onModeSelected("event"));
        custom.setOnClickListener(v -> viewModel.onModeSelected("custom"));
    }
}
