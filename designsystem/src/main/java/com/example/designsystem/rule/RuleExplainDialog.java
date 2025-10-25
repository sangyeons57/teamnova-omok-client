package com.example.designsystem.rule;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.example.application.port.in.UResult;
import com.example.application.usecase.FindRuleByCodeUseCase;
import com.example.application.usecase.RuleIconSource;
import com.example.application.usecase.ResolveRuleIconSourceUseCase;
import com.example.core_di.UseCaseContainer;
import com.example.designsystem.R;
import com.example.domain.rules.Rule;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Simple dialog that explains the selected optional game rule.
 */
public class RuleExplainDialog extends DialogFragment {

    private static final String ARG_RULE_CODE = "arg_rule_code";
    private static final String TAG = "RuleExplainDialog";

    private ExecutorService executor;
    private Handler mainHandler;
    private FindRuleByCodeUseCase findRuleByCodeUseCase;
    private ResolveRuleIconSourceUseCase resolveRuleIconSourceUseCase;

    public static RuleExplainDialog newInstance(@NonNull String ruleCode) {
        RuleExplainDialog dialog = new RuleExplainDialog();
        Bundle args = new Bundle();
        args.putString(ARG_RULE_CODE, Objects.requireNonNull(ruleCode, "ruleCode == null"));
        dialog.setArguments(args);
        return dialog;
    }

    public static void present(@NonNull FragmentManager fragmentManager, @NonNull String ruleCode) {
        if (fragmentManager.findFragmentByTag(TAG) instanceof RuleExplainDialog) {
            return;
        }
        RuleExplainDialog dialog = newInstance(ruleCode);
        dialog.show(fragmentManager, TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UseCaseContainer container = UseCaseContainer.getInstance();
        findRuleByCodeUseCase = container.findRuleByCodeUseCase;
        resolveRuleIconSourceUseCase = container.resolveRuleIconSourceUseCase;
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View view = inflater.inflate(R.layout.dialog_rule_explain, null, false);

        MaterialButton closeButton = view.findViewById(R.id.buttonRuleDialogClose);
        closeButton.setOnClickListener(v -> dismiss());

        bindPlaceholder(view);

        String ruleCode = requireArguments().getString(ARG_RULE_CODE);
        if (ruleCode != null && !ruleCode.trim().isEmpty()) {
            loadRuleDetails(view, ruleCode.trim());
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public void onDestroy() {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
        super.onDestroy();
    }

    private void bindPlaceholder(@NonNull View view) {
        TextView nameView = view.findViewById(R.id.textRuleName);
        TextView descriptionView = view.findViewById(R.id.textRuleDescription);
        ImageView iconView = view.findViewById(R.id.imageRuleIcon);
        nameView.setText(R.string.designsystem_rule_dialog_unknown_title);
        descriptionView.setText(R.string.designsystem_rule_dialog_unknown_description);
        iconView.setImageDrawable(null);
        iconView.setBackgroundResource(R.drawable.bg_rule_icon_placeholder);
    }

    private void loadRuleDetails(@NonNull View view, @NonNull String ruleCode) {
        if (executor == null) {
            return;
        }
        executor.execute(() -> {
            Rule item = null;
            RuleIconSource iconSource = null;
            if (findRuleByCodeUseCase != null) {
                UResult<Rule> result = findRuleByCodeUseCase.execute(ruleCode);
                if (result instanceof UResult.Ok) {
                    @SuppressWarnings("unchecked")
                    UResult.Ok<Rule> ok = (UResult.Ok<Rule>) result;
                    item = ok.value();
                }
            }
            if (resolveRuleIconSourceUseCase != null) {
                UResult<RuleIconSource> iconResult = resolveRuleIconSourceUseCase.execute(ruleCode);
                if (iconResult instanceof UResult.Ok) {
                    @SuppressWarnings("unchecked")
                    UResult.Ok<RuleIconSource> ok = (UResult.Ok<RuleIconSource>) iconResult;
                    iconSource = ok.value();
                }
            }
            Rule finalItem = item;
            RuleIconSource finalIconSource = iconSource;
            mainHandler.post(() -> applyRuleDetails(view, ruleCode, finalItem, finalIconSource));
        });
    }

    private void applyRuleDetails(@NonNull View view,
                                  @NonNull String ruleCode,
                                  @Nullable Rule item,
                                  @Nullable RuleIconSource iconSource) {
        if (!isAdded()) {
            return;
        }
        TextView nameView = view.findViewById(R.id.textRuleName);
        TextView descriptionView = view.findViewById(R.id.textRuleDescription);
        if (item != null) {
            nameView.setText(item.getName());
            descriptionView.setText(item.getDescription());
        } else {
            nameView.setText(R.string.designsystem_rule_dialog_unknown_title);
            descriptionView.setText(R.string.designsystem_rule_dialog_unknown_description);
        }
        View iconContainer = view.findViewById(R.id.imageRuleIcon);
        RuleIconRenderer.bindIconView(iconContainer, ruleCode, item, iconSource);
    }
}
