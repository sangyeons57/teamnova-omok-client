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
import com.example.feature_game.game.presentation.model.GameInfoCard;
import com.example.feature_game.game.presentation.viewmodel.GameInfoDialogViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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

        MaterialTextView labelOne = root.findViewById(R.id.textInfoOneLabel);
        MaterialTextView labelTwo = root.findViewById(R.id.textInfoTwoLabel);
        MaterialTextView labelThree = root.findViewById(R.id.textInfoThreeLabel);
        MaterialTextView labelFour = root.findViewById(R.id.textInfoFourLabel);

        MaterialTextView nameOne = root.findViewById(R.id.textInfoOneName);
        MaterialTextView nameTwo = root.findViewById(R.id.textInfoTwoName);
        MaterialTextView nameThree = root.findViewById(R.id.textInfoThreeName);
        MaterialTextView nameFour = root.findViewById(R.id.textInfoFourName);

        MaterialButton closeButton = root.findViewById(R.id.buttonCloseInfo);

        viewModel.getInfoCards().observe(activity, cards -> bindCards(cards, activity,
                new MaterialCardView[]{cardOne, cardTwo, cardThree, cardFour},
                new MaterialTextView[]{labelOne, labelTwo, labelThree, labelFour},
                new MaterialTextView[]{nameOne, nameTwo, nameThree, nameFour},
                viewModel));

        viewModel.getDismissEvent().observe(activity, dismiss -> {
            if (dismiss == null || !dismiss) {
                return;
            }
            dialog.dismiss();
            viewModel.onEventHandled();
        });

        closeButton.setOnClickListener(v -> viewModel.onCloseClicked());
    }

    private void bindCards(@NonNull List<GameInfoCard> cards,
                           @NonNull FragmentActivity activity,
                           @NonNull MaterialCardView[] cardViews,
                           @NonNull MaterialTextView[] labelViews,
                           @NonNull MaterialTextView[] nameViews,
                           @NonNull GameInfoDialogViewModel viewModel) {
        int count = Math.min(cards.size(), cardViews.length);
        for (int i = 0; i < cardViews.length; i++) {
            MaterialCardView cardView = cardViews[i];
            MaterialTextView labelView = labelViews[i];
            MaterialTextView nameView = nameViews[i];
            if (i < count) {
                GameInfoCard card = cards.get(i);
                labelView.setText(card.getTitleResId());
                nameView.setText(card.getDescriptionResId());
                cardView.setOnClickListener(v -> viewModel.onCardSelected(card));
                cardView.setVisibility(View.VISIBLE);
            } else {
                cardView.setOnClickListener(null);
                cardView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
