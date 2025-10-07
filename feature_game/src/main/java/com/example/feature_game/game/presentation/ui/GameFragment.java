package com.example.feature_game.game.presentation.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.core.dialog.DialogHost;
import com.example.core.dialog.DialogHostOwner;
import com.example.core.dialog.MainDialogType;
import com.example.core.navigation.FragmentNavigationHostOwner;
import com.example.feature_game.R;
import com.example.feature_game.game.di.GameViewModelFactory;
import com.example.feature_game.game.presentation.model.GamePlayerSlot;
import com.example.feature_game.game.presentation.state.GameViewEvent;
import com.example.feature_game.game.presentation.util.ProfileIconResolver;
import com.example.feature_game.game.presentation.viewmodel.GameViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the Omok battle board with participant slots and contextual controls.
 */
public class GameFragment extends Fragment {

    private static final long INFO_AUTO_DISMISS_DELAY_MS = 5_000L;

    private DialogHost<MainDialogType> dialogHost;
    private GameViewModel viewModel;
    private final List<PlayerSlotView> slotViews = new ArrayList<>(4);
    private List<GamePlayerSlot> latestSlots = new ArrayList<>();
    private int latestActiveIndex = 0;
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

        if (!(context instanceof FragmentNavigationHostOwner<?>)) {
            throw new IllegalStateException("Host must provide FragmentNavigationHost");
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
        PlayerSlotView topLeft = new PlayerSlotView(
                root.findViewById(R.id.cardPlayerTopLeft),
                root.findViewById(R.id.imagePlayerTopLeft),
                root.findViewById(R.id.textPlayerTopLeft)
        );
        PlayerSlotView topRight = new PlayerSlotView(
                root.findViewById(R.id.cardPlayerTopRight),
                root.findViewById(R.id.imagePlayerTopRight),
                root.findViewById(R.id.textPlayerTopRight)
        );
        PlayerSlotView bottomLeft = new PlayerSlotView(
                root.findViewById(R.id.cardPlayerBottomLeft),
                root.findViewById(R.id.imagePlayerBottomLeft),
                root.findViewById(R.id.textPlayerBottomLeft)
        );
        PlayerSlotView bottomRight = new PlayerSlotView(
                root.findViewById(R.id.cardPlayerBottomRight),
                root.findViewById(R.id.imagePlayerBottomRight),
                root.findViewById(R.id.textPlayerBottomRight)
        );
        slotViews.clear();
        slotViews.add(topLeft);
        slotViews.add(topRight);
        slotViews.add(bottomLeft);
        slotViews.add(bottomRight);

        View board = root.findViewById(R.id.cardBoardPlaceholder);
        board.setOnClickListener(v -> viewModel.onBoardTapped());

        MaterialButton infoNavigation = root.findViewById(R.id.buttonGameInfoNavigation);

        infoNavigation.setOnClickListener(v -> viewModel.onInfoButtonClicked());
    }

    private void observeViewModel() {
        viewModel.getPlayerSlots().observe(getViewLifecycleOwner(), slots -> {
            if (slots == null) {
                return;
            }
            latestSlots = slots;
            updatePlayerSlots();
        });

        viewModel.getActivePlayerIndex().observe(getViewLifecycleOwner(), index -> {
            latestActiveIndex = index != null ? index : 0;
            updateTurnIndicator();
        });

        viewModel.getViewEvents().observe(getViewLifecycleOwner(), event -> {
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
                default:
                    break;
            }
            viewModel.onEventHandled();
        });
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

    private static final class PlayerSlotView {
        private final MaterialCardView container;
        private final ImageView avatarView;
        private final MaterialTextView nameView;

        private PlayerSlotView(@NonNull MaterialCardView container,
                               @NonNull ImageView avatarView,
                               @NonNull MaterialTextView nameView) {
            this.container = container;
            this.avatarView = avatarView;
            this.nameView = nameView;
        }

        void update(@NonNull GamePlayerSlot slot) {
            avatarView.setImageResource(ProfileIconResolver.resolve(slot.getProfileIconCode()));
            if (slot.isEmpty()) {
                nameView.setText(R.string.game_player_empty_slot);
                avatarView.setAlpha(0.4f);
            } else if (slot.getPosition() == 0 && slot.getDisplayName().isEmpty()) {
                nameView.setText(R.string.game_player_self_placeholder);
                avatarView.setAlpha(1f);
            } else {
                nameView.setText(slot.getDisplayName());
                avatarView.setAlpha(1f);
            }
            container.setEnabled(slot.isEnabled());
            float alpha = slot.isEnabled() ? 1f : 0.4f;
            container.setAlpha(alpha);
        }

        void setActive(boolean active, boolean enabled) {
            int background;
            if (!enabled) {
                background = MaterialColors.getColor(container, com.google.android.material.R.attr.colorSurfaceVariant);
            } else if (active) {
                background = MaterialColors.getColor(container, com.google.android.material.R.attr.colorPrimaryContainer);
            } else {
                background = MaterialColors.getColor(container, com.google.android.material.R.attr.colorSurfaceVariant);
            }
            container.setCardBackgroundColor(background);
            int strokeColor = active
                    ? MaterialColors.getColor(container, androidx.appcompat.R.attr.colorPrimary)
                    : MaterialColors.getColor(container, com.google.android.material.R.attr.colorOutline);
            container.setStrokeColor(strokeColor);
            container.setStrokeWidth(active ? 6 : 2);
        }
    }
}
