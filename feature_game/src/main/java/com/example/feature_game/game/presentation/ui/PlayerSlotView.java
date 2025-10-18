package com.example.feature_game.game.presentation.ui;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.application.session.postgame.PlayerDisconnectReason;
import com.example.core.sprite.SpriteLayout;
import com.example.core.sprite.SpriteSheet;
import com.example.core.sprite.SpriteView;
import com.example.core.sprite.provider.CircleClip;
import com.example.designsystem.ProfileSpriteSheetProvider;
import com.example.feature_game.R;
import com.example.feature_game.game.presentation.model.GamePlayerSlot;
import com.example.feature_game.game.presentation.util.ProfileIconResolver;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;
import com.google.android.material.textview.MaterialTextView;

public class PlayerSlotView {
    private final MaterialCardView container;
    private final SpriteView avatarView;
    private final MaterialTextView nameView;
    private boolean disconnected = false;
    private boolean emptySlot = true;
    private PlayerDisconnectReason disconnectReason = PlayerDisconnectReason.UNKNOWN;

    private PlayerSlotView(@NonNull MaterialCardView container,
                           @NonNull SpriteView avatarView,
                           @NonNull MaterialTextView nameView) {
        this.container = container;
        this.avatarView = avatarView;
        this.nameView = nameView;


        this.avatarView.setClipPathProvider(new CircleClip());
        this.avatarView.setScaleMode(SpriteView.ScaleMode.FIT_CENTER);
    }

    public static PlayerSlotView create(@NonNull MaterialCardView container, @NonNull SpriteView avatarView, @NonNull MaterialTextView nameView) {
        return new PlayerSlotView(container, avatarView, nameView);
    }

    void update(@NonNull GamePlayerSlot slot) {
        SpriteSheet sheet = ProfileSpriteSheetProvider.get(avatarView.getContext());
        avatarView.setSpriteSheet(sheet);
        avatarView.setIndex(slot.getProfileIconCode());

        disconnected = slot.isDisconnected();
        disconnectReason = slot.getDisconnectReason();
        emptySlot = slot.isEmpty();
        if (slot.isEmpty()) {
            nameView.setText(R.string.game_player_empty_slot);
        } else if (slot.getPosition() == 0 && slot.getDisplayName().isEmpty()) {
            nameView.setText(R.string.game_player_self_placeholder);
        } else {
            nameView.setText(slot.getDisplayName());
        }
        setActive(false, slot.isEnabled());
    }

    void setActive(boolean active, boolean enabled) {
        boolean effectiveEnabled = enabled && !disconnected;
        container.setEnabled(effectiveEnabled);
        float alpha = effectiveEnabled
                ? 1f
                : (disconnected ? 1f : 0.4f);
        container.setAlpha(alpha);

        int background;
        if (disconnected) {
            background = MaterialColors.getColor(container, com.google.android.material.R.attr.colorErrorContainer);
        } else if (!effectiveEnabled) {
            background = MaterialColors.getColor(container, com.google.android.material.R.attr.colorSurfaceVariant);
        } else if (active) {
            background = MaterialColors.getColor(container, com.google.android.material.R.attr.colorPrimaryContainer);
        } else {
            background = MaterialColors.getColor(container, com.google.android.material.R.attr.colorSurfaceVariant);
        }
        container.setCardBackgroundColor(background);

        int strokeColor;
        if (disconnected) {
            strokeColor = MaterialColors.getColor(container, com.google.android.material.R.attr.colorOnError);
        } else if (active) {
            strokeColor = MaterialColors.getColor(container, androidx.appcompat.R.attr.colorPrimary);
        } else {
            strokeColor = MaterialColors.getColor(container, com.google.android.material.R.attr.colorOutline);
        }
        container.setStrokeColor(strokeColor);
        container.setStrokeWidth(disconnected ? 4 : active ? 6 : 2);

        int nameColor = disconnected
                ? MaterialColors.getColor(container, com.google.android.material.R.attr.colorOnErrorContainer)
                : MaterialColors.getColor(container, com.google.android.material.R.attr.colorOnSurface);
        nameView.setTextColor(nameColor);

        float avatarAlpha;
        if (emptySlot) {
            avatarAlpha = 0.4f;
        } else if (disconnected) {
            avatarAlpha = 0.6f;
        } else {
            avatarAlpha = 1f;
        }
        avatarView.setAlpha(avatarAlpha);
    }
}
