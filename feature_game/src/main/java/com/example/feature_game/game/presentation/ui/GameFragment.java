package com.example.feature_game.game.presentation.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.application.session.OmokBoardState;
import com.example.application.session.OmokStonePlacement;
import com.example.application.session.OmokStoneType;
import com.example.core_api.dialog.DialogHost;
import com.example.core_api.dialog.DialogHostOwner;
import com.example.core_api.dialog.MainDialogType;
import com.example.core_di.sound.SoundEffects;
import com.example.feature_game.R;
import com.example.feature_game.game.di.GameViewModelFactory;
import com.example.feature_game.game.presentation.model.GamePlayerSlot;
import com.example.feature_game.game.presentation.state.GameViewEvent;
import com.example.feature_game.game.presentation.viewmodel.GameViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Displays the Omok battle board with participant slots and contextual controls.
 */
public class GameFragment extends Fragment {

    private static final long INFO_AUTO_DISMISS_DELAY_MS = 5_000L;
    private static final int DEFAULT_BOARD_SIZE = 10;

    private DialogHost<MainDialogType> dialogHost;
    private GameViewModel viewModel;
    private final List<PlayerSlotView> slotViews = new ArrayList<>(4);
    private List<GamePlayerSlot> latestSlots = new ArrayList<>();
    private int latestActiveIndex = 0;
    private OmokBoardView boardView;
    private MaterialTextView remainingTimeText;
    private View rootContainer;
    private int defaultBackgroundColor;
    private int selfTurnBackgroundColor;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pendingAutoDismiss;

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DialogHostOwner<?>) {
            dialogHost = ((DialogHostOwner<MainDialogType>) context).getDialogHost();
        } else {
            throw new IllegalStateException("Host must implement DialogHostOwner");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game, container, false);
    }

    @Override
    public void onDestroyView() {
        clearPendingAutoDismiss();
        slotViews.clear();
        boardView = null;
        rootContainer = null;
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        dialogHost = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        GameViewModelFactory factory = GameViewModelFactory.create();
        viewModel = new ViewModelProvider(this, factory).get(GameViewModel.class);

        bindViews(view);
        observeViewModel();
    }

    private void bindViews(@NonNull View root) {
        rootContainer = root.findViewById(R.id.layoutGameRoot);
        if (rootContainer != null) {
            defaultBackgroundColor = MaterialColors.getColor(rootContainer, com.google.android.material.R.attr.colorSurface);
            selfTurnBackgroundColor = MaterialColors.getColor(rootContainer, com.google.android.material.R.attr.colorPrimaryContainer);
            updateRootBackground(false);
        }

        PlayerSlotView topLeft = PlayerSlotView.create(
                root.findViewById(R.id.cardPlayerTopLeft),
                root.findViewById(R.id.imagePlayerTopLeft),
                root.findViewById(R.id.textPlayerTopLeft)
        );
        PlayerSlotView topRight = PlayerSlotView.create(
                root.findViewById(R.id.cardPlayerTopRight),
                root.findViewById(R.id.imagePlayerTopRight),
                root.findViewById(R.id.textPlayerTopRight)
        );
        PlayerSlotView bottomLeft = PlayerSlotView.create(
                root.findViewById(R.id.cardPlayerBottomLeft),
                root.findViewById(R.id.imagePlayerBottomLeft),
                root.findViewById(R.id.textPlayerBottomLeft)
        );
        PlayerSlotView bottomRight = PlayerSlotView.create(
                root.findViewById(R.id.cardPlayerBottomRight),
                root.findViewById(R.id.imagePlayerBottomRight),
                root.findViewById(R.id.textPlayerBottomRight)
        );
        slotViews.clear();
        slotViews.add(topLeft);
        slotViews.add(topRight);
        slotViews.add(bottomLeft);
        slotViews.add(bottomRight);

        boardView = root.findViewById(R.id.omokBoardView);
        if (boardView != null) {
            boardView.clearStoneDrawables();
            boardView.registerStoneDrawable(OmokStoneType.RED.index, R.drawable.omok_stone_red);
            boardView.registerStoneDrawable(OmokStoneType.BLUE.index, R.drawable.omok_stone_blue);
            boardView.registerStoneDrawable(OmokStoneType.YELLOW.index, R.drawable.omok_stone_yellow);
            boardView.registerStoneDrawable(OmokStoneType.GREEN.index, R.drawable.omok_stone_green);
            boardView.registerStoneDrawable(OmokStoneType.WHITE.index, R.drawable.omok_stone_white);
            boardView.registerStoneDrawable(OmokStoneType.BLACK.index, R.drawable.omok_stone_black);
            boardView.setOnCellTapListener((x, y) -> viewModel.onBoardCellTapped(x, y));
        }

        remainingTimeText = root.findViewById(R.id.textRemainingTime);
        Integer currentRemaining = viewModel.getRemainingSeconds().getValue();
        updateRemainingTime(currentRemaining != null ? currentRemaining : 0);

        MaterialButton infoNavigation = root.findViewById(R.id.buttonGameInfoNavigation);

        infoNavigation.setOnClickListener(v -> {
            SoundEffects.playButtonClick();
            viewModel.onInfoButtonClicked();
        });
    }

    private void observeViewModel() {
        viewModel.getPlayerSlots().observe(getViewLifecycleOwner(), this::playerSlotObserve);
        viewModel.getActivePlayerIndex().observe(getViewLifecycleOwner(), this::activePlayerIndexObserve);
        viewModel.getRemainingSeconds().observe(getViewLifecycleOwner(), this::remainingSecondsObserve);
        viewModel.getBoardState().observe(getViewLifecycleOwner(), this::renderBoard);
        viewModel.getSelfTurnHighlight().observe(getViewLifecycleOwner(), this::selfTurnHighlightObserve);
        viewModel.getViewEvents().observe(getViewLifecycleOwner(), this::viewEventObserve);
    }


    private void renderBoard(@Nullable OmokBoardState state) {
        if (boardView == null) {
            return;
        }
        if (state == null || state.getWidth() <= 0 || state.getHeight() <= 0) {
            boardView.setBoardSize(DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE);
            boardView.clearBoard();
            return;
        }
        int width = state.getWidth();
        int height = state.getHeight();
        int[] indices = new int[width * height];
        Arrays.fill(indices, -1);
        for (OmokStonePlacement placement : state.getPlacements()) {
            int mappedIndex = placement.getStoneType().index;
            if (mappedIndex < 0) {
                continue;
            }
            int flattened = placement.getY() * width + placement.getX();
            if (flattened >= 0 && flattened < indices.length) {
                indices[flattened] = mappedIndex;
            }
        }
        boardView.setBoardState(width, height, indices);
    }

    private void updateRemainingTime(int seconds) {
        if (remainingTimeText == null) {
            return;
        }
        int clamped = Math.max(0, seconds);
        String formatted = String.format(Locale.getDefault(), "%02d", clamped % 60);
        remainingTimeText.setText(formatted);
    }

    private void selfTurnHighlightObserve(@Nullable Boolean active) {
        boolean highlight = active != null && active;
        updateRootBackground(highlight);
    }

    private void updateRootBackground(boolean highlightActive) {
        if (rootContainer == null) {
            return;
        }
        int color = highlightActive ? selfTurnBackgroundColor : defaultBackgroundColor;
        rootContainer.setBackgroundColor(color);
    }


    private void updatePlayerSlots() {
        for (GamePlayerSlot slot : latestSlots) {
            if (slot.getPosition() < 0 || slot.getPosition() >= slotViews.size()) {
                continue;
            }
            PlayerSlotView holder = slotViews.get(slot.getPosition());
            holder.update(slot);
        }
        updateTurnIndicator();
    }

    private void updateTurnIndicator() {
        int enabledCount = 0;
        GamePlayerSlot activeSlot = null;
        for (GamePlayerSlot slot : latestSlots) {
            if (!slot.isEnabled()) {
                continue;
            }
            if (enabledCount == latestActiveIndex) {
                activeSlot = slot;
            }
            enabledCount++;
        }

        for (GamePlayerSlot slot : latestSlots) {
            if (slot.getPosition() < 0 || slot.getPosition() >= slotViews.size()) {
                continue;
            }
            boolean isActive = activeSlot != null && slot.getPosition() == activeSlot.getPosition();
            slotViews.get(slot.getPosition()).setActive(isActive, slot.isEnabled());
        }
    }

    private void showGameInfoDialog(boolean autoDismiss) {
        enqueueDialog(MainDialogType.GAME_INFO);
        if (autoDismiss) {
            scheduleAutoDismiss(MainDialogType.GAME_INFO);
        } else {
            clearPendingAutoDismiss();
        }
    }

    private void scheduleAutoDismiss(@NonNull MainDialogType type) {
        clearPendingAutoDismiss();
        pendingAutoDismiss = () -> {
            dismissDialog(type);
            pendingAutoDismiss = null;
            viewModel.notifyGameReady();
        };
        handler.postDelayed(pendingAutoDismiss, INFO_AUTO_DISMISS_DELAY_MS);
    }

    private void clearPendingAutoDismiss() {
        if (pendingAutoDismiss == null) {
            return;
        }
        handler.removeCallbacks(pendingAutoDismiss);
        pendingAutoDismiss = null;
    }

    private void dismissDialog(@NonNull MainDialogType type) {
        if (dialogHost == null || !dialogHost.isAttached()) {
            return;
        }
        dialogHost.dismiss(type);
    }

    private void enqueueDialog(@NonNull MainDialogType type) {
        if (dialogHost == null || !dialogHost.isAttached()) {
            return;
        }
        dialogHost.enqueue(type);
    }

    private void playerSlotObserve(List<GamePlayerSlot> slots) {
        if (slots == null) {
            return;
        }
        latestSlots = slots;
        updatePlayerSlots();
    }

    private void activePlayerIndexObserve(Integer index) {
        latestActiveIndex = index != null ? index : 0;
        updateTurnIndicator();
    }

    private void remainingSecondsObserve(Integer seconds) {
        int value = seconds != null ? seconds : 0;
        updateRemainingTime(value);
    }

    private void viewEventObserve(GameViewEvent event) {
        if (event == null) {
            return;
        }
        switch (event) {
            case OPEN_GAME_INFO_DIALOG:
                showGameInfoDialog(false);
                break;
            case AUTO_OPEN_GAME_INFO_DIALOG:
                showGameInfoDialog(true);
                break;
            case OPEN_GAME_RESULT_DIALOG:
                enqueueDialog(MainDialogType.GAME_RESULT);
                break;
            case OPEN_POST_GAME_SCREEN:
                enqueueDialog(MainDialogType.POST_GAME);
                break;
            case SHOW_TURN_TIMEOUT_MESSAGE:
                Toast.makeText(requireContext(), R.string.game_turn_timeout_message, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        viewModel.onEventHandled();
    }

}
