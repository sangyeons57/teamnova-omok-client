package com.example.designsystem.rule;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.core.rules.RuleCatalog;
import com.example.core.rules.RuleInfo;
import com.example.designsystem.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Simple dialog that explains the selected optional game rule.
 */
public class RuleExplainDialog extends DialogFragment {

    private static final String ARG_RULE_ID = "arg_rule_id";
    private static final String TAG = "RuleExplainDialog";

    public static RuleExplainDialog newInstance(int ruleId) {
        RuleExplainDialog dialog = new RuleExplainDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_RULE_ID, ruleId);
        dialog.setArguments(args);
        return dialog;
    }

    public static void show(@NonNull FragmentManager fragmentManager, int ruleId) {
        if (fragmentManager.findFragmentByTag(TAG) instanceof RuleExplainDialog) {
            return;
        }
        RuleExplainDialog dialog = newInstance(ruleId);
        dialog.show(fragmentManager, TAG);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.dialog_rule_explain, null, false);

        int ruleId = requireArguments().getInt(ARG_RULE_ID, -1);
        RuleInfo ruleInfo = RuleCatalog.findRule(ruleId);

        bindRule(view, ruleInfo);

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    private void bindRule(@NonNull View view, @Nullable RuleInfo ruleInfo) {
        TextView nameView = view.findViewById(R.id.textRuleName);
        TextView descriptionView = view.findViewById(R.id.textRuleDescription);
        ImageView iconView = view.findViewById(R.id.imageRuleIcon);
        MaterialButton closeButton = view.findViewById(R.id.buttonRuleDialogClose);

        if (ruleInfo == null) {
            nameView.setText(R.string.designsystem_rule_dialog_unknown_title);
            descriptionView.setText(R.string.designsystem_rule_dialog_unknown_description);
            iconView.setImageDrawable(null);
            iconView.setBackgroundResource(R.drawable.bg_rule_icon_placeholder);
        } else {
            nameView.setText(ruleInfo.getNameRes());
            descriptionView.setText(ruleInfo.getDescriptionRes());
            iconView.setBackgroundResource(R.drawable.bg_rule_icon_placeholder);
            @DrawableRes int iconRes = ruleInfo.getIconRes();
            if (iconRes != RuleInfo.NO_ICON) {
                iconView.setImageResource(iconRes);
            } else {
                iconView.setImageDrawable(null);
            }
            iconView.setContentDescription(nameView.getText());
        }

        closeButton.setOnClickListener(v -> dismiss());
    }
}
