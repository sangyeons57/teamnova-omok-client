package com.example.feature_game.game.presentation.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.feature_game.R;
import com.example.feature_game.game.presentation.model.GameInfoCard;

import java.util.Arrays;
import java.util.List;

/**
 * Supplies static instructional content for the game info dialog.
 */
public class GameInfoDialogViewModel extends ViewModel {

    private final MutableLiveData<List<GameInfoCard>> infoCards = new MutableLiveData<>();
    private final MutableLiveData<Boolean> dismissEvent = new MutableLiveData<>();

    public GameInfoDialogViewModel() {
        infoCards.setValue(Arrays.asList(
                new GameInfoCard(R.string.game_info_card_rules_title, R.string.game_info_card_rules_description),
                new GameInfoCard(R.string.game_info_card_controls_title, R.string.game_info_card_controls_description),
                new GameInfoCard(R.string.game_info_card_scoring_title, R.string.game_info_card_scoring_description),
                new GameInfoCard(R.string.game_info_card_tips_title, R.string.game_info_card_tips_description)
        ));
    }

    @NonNull
    public LiveData<List<GameInfoCard>> getInfoCards() {
        return infoCards;
    }

    @NonNull
    public LiveData<Boolean> getDismissEvent() {
        return dismissEvent;
    }

    public void onCardSelected(@NonNull GameInfoCard card) {
        // Reserved for future deep-link behaviour (e.g., show additional instruction screen).
    }

    public void onCloseClicked() {
        dismissEvent.setValue(true);
    }

    public void onEventHandled() {
        dismissEvent.setValue(null);
    }
}
