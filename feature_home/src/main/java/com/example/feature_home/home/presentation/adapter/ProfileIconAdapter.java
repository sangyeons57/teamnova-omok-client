package com.example.feature_home.home.presentation.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feature_home.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.List;

/**
 * Simple adapter that renders a fixed set of selectable profile icon slots.
 */
public final class ProfileIconAdapter extends RecyclerView.Adapter<ProfileIconAdapter.IconViewHolder> {

    /**
     * Callback for notifying the consumer when an item is tapped.
     */
    public interface OnIconClickListener {
        void onIconClick(int iconCode);
    }

    private static final List<IconItem> ICONS = Arrays.asList(
            new IconItem(0, R.color.profile_icon_color_0, R.string.dialog_setting_profile_icon_0),
            new IconItem(1, R.color.profile_icon_color_1, R.string.dialog_setting_profile_icon_1),
            new IconItem(2, R.color.profile_icon_color_2, R.string.dialog_setting_profile_icon_2),
            new IconItem(3, R.color.profile_icon_color_3, R.string.dialog_setting_profile_icon_3),
            new IconItem(4, R.color.profile_icon_color_4, R.string.dialog_setting_profile_icon_4),
            new IconItem(5, R.color.profile_icon_color_5, R.string.dialog_setting_profile_icon_5),
            new IconItem(6, R.color.profile_icon_color_6, R.string.dialog_setting_profile_icon_6),
            new IconItem(7, R.color.profile_icon_color_7, R.string.dialog_setting_profile_icon_7),
            new IconItem(8, R.color.profile_icon_color_8, R.string.dialog_setting_profile_icon_8),
            new IconItem(9, R.color.profile_icon_color_9, R.string.dialog_setting_profile_icon_9)
    );

    private final OnIconClickListener listener;
    private int selectedIcon = NO_SELECTION;

    private static final int NO_SELECTION = -1;

    public ProfileIconAdapter(@NonNull OnIconClickListener listener) {
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return ICONS.get(position).iconCode;
    }

    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_profile_icon, parent, false);
        return new IconViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        IconItem item = ICONS.get(position);
        holder.bind(item, item.iconCode == selectedIcon);
    }

    @Override
    public int getItemCount() {
        return ICONS.size();
    }

    /**
     * Updates the highlighted selection and refreshes the affected rows.
     */
    public void setSelectedIcon(@Nullable Integer iconCode) {
        int newSelection = iconCode == null ? NO_SELECTION : iconCode;
        if (selectedIcon == newSelection) {
            return;
        }
        int previousIndex = findIndexByIcon(selectedIcon);
        selectedIcon = newSelection;
        if (previousIndex >= 0) {
            notifyItemChanged(previousIndex);
        }
        int newIndex = findIndexByIcon(newSelection);
        if (newIndex >= 0) {
            notifyItemChanged(newIndex);
        }
    }

    private int findIndexByIcon(int iconCode) {
        if (iconCode == NO_SELECTION) {
            return -1;
        }
        for (int i = 0; i < ICONS.size(); i++) {
            if (ICONS.get(i).iconCode == iconCode) {
                return i;
            }
        }
        return -1;
    }

    static final class IconViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final MaterialButton button;
        private final OnIconClickListener listener;

        IconViewHolder(@NonNull View itemView, @NonNull OnIconClickListener listener) {
            super(itemView);
            this.listener = listener;
            this.cardView = itemView.findViewById(R.id.cardProfileIcon);
            this.button = itemView.findViewById(R.id.buttonProfileIcon);
        }

        void bind(@NonNull IconItem item, boolean isSelected) {
            Context context = itemView.getContext();
            button.setText(String.valueOf(item.iconCode));
            button.setContentDescription(context.getString(item.descriptionRes));

            ColorStateList tint = ContextCompat.getColorStateList(context, item.colorRes);
            button.setBackgroundTintList(tint);

            int strokeColor = isSelected
                    ? ContextCompat.getColor(context, com.example.designsystem.R.color.md_theme_light_primary)
                    : ContextCompat.getColor(context, android.R.color.transparent);
            int strokeWidth = context.getResources().getDimensionPixelSize(
                    isSelected
                            ? R.dimen.profile_icon_item_stroke_width_selected
                            : R.dimen.profile_icon_item_stroke_width);
            cardView.setStrokeColor(strokeColor);
            cardView.setStrokeWidth(strokeWidth);

            button.setOnClickListener(v -> listener.onIconClick(item.iconCode));
        }
    }

    private record IconItem(int iconCode, @ColorRes int colorRes, @StringRes int descriptionRes) {
    }
}
