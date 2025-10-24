package com.example.designsystem.rule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.core_api.rules.RuleCatalog;
import com.example.core_api.rules.RuleInfo;
import com.example.designsystem.R;

/**
 * Factory helpers for rendering rule icons consistently across modules.
 */
public final class RuleIconRenderer {

    private RuleIconRenderer() {
    }

    @NonNull
    public static View createIconView(@NonNull Context context, int ruleId, @Nullable ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View iconView = inflater.inflate(R.layout.item_rule_icon_button, parent, false);
        ImageView imageView = iconView.findViewById(R.id.imageRuleIcon);
        RuleInfo info = RuleCatalog.findRule(ruleId);
        if (info != null && info.hasIcon()) {
            imageView.setImageResource(info.getIconRes());
        } else {
            imageView.setImageDrawable(null);
        }
        CharSequence label = resolveRuleLabel(context, info);
        iconView.setContentDescription(label);
        imageView.setContentDescription(label);
        iconView.setTag(info);
        return iconView;
    }

    @NonNull
    public static CharSequence resolveRuleLabel(@NonNull Context context, @Nullable RuleInfo info) {
        if (info == null) {
            return context.getString(R.string.designsystem_rule_dialog_unknown_title);
        }
        return context.getString(info.getNameRes());
    }
}
