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

import com.example.core_api.dialog.DialogController;
import com.example.core_api.dialog.DialogRequest;
import com.example.core_api.dialog.MainDialogType;
import com.example.application.session.GameMode;
import com.example.core_di.sound.SoundEffects;
import com.example.feature_home.home.presentation.viewmodel.GameModeDialogViewModel;
import com.example.feature_home.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.EnumMap;
import java.util.Map;

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
        bindButtons(activity, contentView, dialog, viewModel);
        dialog.setOnShowListener(ignored -> {
            if (dialog.getWindow() != null) {
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
        });
        return dialog;
    }

    private void bindButtons(@NonNull FragmentActivity activity,
                             @NonNull View contentView,
                             @NonNull AlertDialog dialog,
                             @NonNull GameModeDialogViewModel viewModel) {
        MaterialButton close = contentView.findViewById(R.id.buttonGameModeClose);
        MaterialButton free = contentView.findViewById(R.id.buttonGameModeFree);
        MaterialButton twoPlayer = contentView.findViewById(R.id.buttonGameModeTwoPlayer);
        MaterialButton threePlayer = contentView.findViewById(R.id.buttonGameModeThreePlayer);
        MaterialButton fourPlayer = contentView.findViewById(R.id.buttonGameModeFourPlayer);

        close.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            dialog.dismiss();
        });

        Map<GameMode, MaterialButton> buttonMap = new EnumMap<>(GameMode.class);
        buttonMap.put(GameMode.FREE, free);
        buttonMap.put(GameMode.TWO_PLAYER, twoPlayer);
        buttonMap.put(GameMode.THREE_PLAYER, threePlayer);
        buttonMap.put(GameMode.FOUR_PLAYER, fourPlayer);

        int defaultStroke = contentView.getResources().getDimensionPixelSize(R.dimen.game_mode_stroke_width);
        int selectedStroke = contentView.getResources().getDimensionPixelSize(R.dimen.game_mode_stroke_width_selected);

        for (Map.Entry<GameMode, MaterialButton> entry : buttonMap.entrySet()) {
            GameMode mode = entry.getKey();
            MaterialButton button = entry.getValue();
            button.setCheckable(true);
            button.setOnClickListener(v -> {
                SoundEffects.playButtonClick();
                viewModel.onModeSelected(mode);
            });
        }

        updateSelection(buttonMap, viewModel.getCurrentMode(), defaultStroke, selectedStroke);
        viewModel.getModeStream().observe(activity, mode -> updateSelection(buttonMap, mode, defaultStroke, selectedStroke));
    }

    private void updateSelection(@NonNull Map<GameMode, MaterialButton> buttonMap,
                                 @NonNull GameMode selectedMode,
                                 int defaultStroke,
                                 int selectedStroke) {
        for (Map.Entry<GameMode, MaterialButton> entry : buttonMap.entrySet()) {
            MaterialButton button = entry.getValue();
            boolean selected = entry.getKey() == selectedMode;
            button.setChecked(selected);
            button.setStrokeWidth(selected ? selectedStroke : defaultStroke);
        }
    }
}
