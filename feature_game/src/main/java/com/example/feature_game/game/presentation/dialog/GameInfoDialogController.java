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
import com.example.feature_game.game.presentation.model.GameInfoSlot;
import com.example.feature_game.game.presentation.util.ProfileIconResolver;
import com.example.feature_game.game.presentation.viewmodel.GameInfoDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

/**
 * Shows quick-access information cards about the ongoing game.
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
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#B3000000")));
            }
        });
        return dialog;
    }

    private void bind(@NonNull View root,
                      @NonNull AlertDialog dialog,
                      @NonNull GameInfoDialogViewModel viewModel,
                      @NonNull FragmentActivity activity) {
        MaterialCardView cardOne = root.findViewById(R.id.cardInfoOne);
        MaterialCardView cardTwo = root.findViewById(R.id.cardInfoTwo);
        MaterialCardView cardThree = root.findViewById(R.id.cardInfoThree);
        MaterialCardView cardFour = root.findViewById(R.id.cardInfoFour);

        ShapeableImageView imageOne = root.findViewById(R.id.imageInfoOneProfile);
        ShapeableImageView imageTwo = root.findViewById(R.id.imageInfoTwoProfile);
        ShapeableImageView imageThree = root.findViewById(R.id.imageInfoThreeProfile);
        ShapeableImageView imageFour = root.findViewById(R.id.imageInfoFourProfile);

        MaterialTextView labelOne = root.findViewById(R.id.textInfoOneLabel);
        MaterialTextView labelTwo = root.findViewById(R.id.textInfoTwoLabel);
        MaterialTextView labelThree = root.findViewById(R.id.textInfoThreeLabel);
        MaterialTextView labelFour = root.findViewById(R.id.textInfoFourLabel);

        MaterialTextView nameOne = root.findViewById(R.id.textInfoOneName);
        MaterialTextView nameTwo = root.findViewById(R.id.textInfoTwoName);
        MaterialTextView nameThree = root.findViewById(R.id.textInfoThreeName);
        MaterialTextView nameFour = root.findViewById(R.id.textInfoFourName);

        MaterialButton closeButton = root.findViewById(R.id.buttonCloseInfo);

        viewModel.getSlots().observe(activity, slots -> bindSlots(slots,
                new MaterialCardView[]{cardOne, cardTwo, cardThree, cardFour},
                new MaterialTextView[]{labelOne, labelTwo, labelThree, labelFour},
                new MaterialTextView[]{nameOne, nameTwo, nameThree, nameFour},
                new ShapeableImageView[]{imageOne, imageTwo, imageThree, imageFour}));

        viewModel.getDismissEvent().observe(activity, dismiss -> {
            if (dismiss == null || !dismiss) {
                return;
            }
            dialog.dismiss();
            viewModel.onEventHandled();
        });

        closeButton.setOnClickListener(v -> viewModel.onCloseClicked());
    }

    private void bindSlots(@NonNull List<GameInfoSlot> slots,
                           @NonNull MaterialCardView[] cardViews,
                           @NonNull MaterialTextView[] labelViews,
                           @NonNull MaterialTextView[] nameViews,
                           @NonNull ShapeableImageView[] imageViews) {
        int[] labelResIds = {
                R.string.game_info_slot_label_one,
                R.string.game_info_slot_label_two,
                R.string.game_info_slot_label_three,
                R.string.game_info_slot_label_four
        };

        for (int i = 0; i < cardViews.length; i++) {
            MaterialCardView cardView = cardViews[i];
            MaterialTextView labelView = labelViews[i];
            MaterialTextView nameView = nameViews[i];
            ShapeableImageView imageView = imageViews[i];

            labelView.setText(labelResIds[i]);

            GameInfoSlot slot = i < slots.size() ? slots.get(i) : null;
            boolean enabled = slot != null && slot.isEnabled();

            cardView.setEnabled(enabled);
            cardView.setAlpha(enabled ? 1f : 0.4f);
            cardView.setOnClickListener(null);

            int iconRes = ProfileIconResolver.resolve(slot != null ? slot.getProfileIconCode() : 0);
            imageView.setImageResource(iconRes);
            imageView.setAlpha(slot != null && slot.isOccupied() ? 1f : 0.4f);

            if (slot != null && slot.isOccupied() && !slot.getDisplayName().isEmpty()) {
                nameView.setText(slot.getDisplayName());
            } else if (enabled) {
                nameView.setText(R.string.game_info_slot_empty_placeholder);
            } else {
                nameView.setText("");
            }
        }
    }

}
