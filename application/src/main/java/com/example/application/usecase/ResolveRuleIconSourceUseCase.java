package com.example.application.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.util.Log;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.rules.RulesRepository;
import com.example.core_api.exception.UseCaseException;
import com.example.domain.rules.Rule;
import com.example.domain.rules.RuleCode;

import java.util.Objects;

/**
 * Resolves the icon source configuration for a rule.
 */
public final class ResolveRuleIconSourceUseCase extends UseCase<String, RuleIconSource> {

    private static final String ERROR_INVALID_CODE = "RULE_CODE_INVALID";
    private static final String TAG = "ResolveRuleIconSource";

    @NonNull
    private final RulesRepository rulesRepository;

    public ResolveRuleIconSourceUseCase(@NonNull UseCaseConfig config,
                                        @NonNull RulesRepository rulesRepository) {
        super(Objects.requireNonNull(config, "config == null"));
        this.rulesRepository = Objects.requireNonNull(rulesRepository, "rulesRepository == null");
    }

    @Override
    protected RuleIconSource run(String input) throws UseCaseException {
        String normalizedCode = normalizeCode(input);
        Rule rule = rulesRepository.findRuleByCode(RuleCode.of(normalizedCode));
        if (rule == null) {
            Log.w(TAG, "Rule missing for icon resolution code=" + normalizedCode);
            return RuleIconSource.none();
        }
        RuleIconSource source = RuleIconSourceMapper.fromPath(rule.getIconPath());
        Log.d(TAG, "Icon resolved code=" + normalizedCode + " type=" + source.getType() + " value=" + source.getValue());
        return source;
    }

    @NonNull
    private String normalizeCode(String rawCode) throws UseCaseException {
        if (rawCode == null) {
            Log.w(TAG, "normalizeCode: rawCode is null");
            throw UseCaseException.of(ERROR_INVALID_CODE, "규칙 코드는 필수입니다.");
        }
        String trimmed = rawCode.trim();
        if (trimmed.isEmpty()) {
            Log.w(TAG, "normalizeCode: rawCode is empty");
            throw UseCaseException.of(ERROR_INVALID_CODE, "규칙 코드는 비어 있을 수 없습니다.");
        }
        return trimmed;
    }

}
