package com.example.application.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.rules.RulesRepository;
import com.example.core_api.exception.UseCaseException;
import com.example.domain.rules.Rule;
import com.example.domain.rules.RuleCode;

import java.util.Locale;
import java.util.Objects;

/**
 * Resolves the icon source configuration for a rule.
 */
public final class ResolveRuleIconSourceUseCase extends UseCase<String, RuleIconSource> {

    private static final String ERROR_INVALID_CODE = "RULE_CODE_INVALID";

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
            return RuleIconSource.none();
        }
        return mapIconPath(rule.getIconPath());
    }

    @NonNull
    private String normalizeCode(String rawCode) throws UseCaseException {
        if (rawCode == null) {
            throw UseCaseException.of(ERROR_INVALID_CODE, "규칙 코드는 필수입니다.");
        }
        String trimmed = rawCode.trim();
        if (trimmed.isEmpty()) {
            throw UseCaseException.of(ERROR_INVALID_CODE, "규칙 코드는 비어 있을 수 없습니다.");
        }
        return trimmed;
    }

    @NonNull
    private RuleIconSource mapIconPath(@Nullable String rawPath) {
        if (rawPath == null) {
            return RuleIconSource.none();
        }
        String trimmed = rawPath.trim();
        if (trimmed.isEmpty()) {
            return RuleIconSource.none();
        }
        if (trimmed.startsWith("asset://")) {
            return RuleIconSource.asset(trimmed.substring("asset://".length()));
        }
        if (trimmed.startsWith("@drawable/")) {
            return RuleIconSource.drawableResource(trimmed.substring("@drawable/".length()));
        }
        if (trimmed.startsWith("res://drawable/")) {
            return RuleIconSource.drawableResource(trimmed.substring("res://drawable/".length()));
        }
        // Fallback heuristics: treat dotted or slash paths as assets, others as drawables.
        if (trimmed.contains("/") || trimmed.toLowerCase(Locale.US).endsWith(".png")
                || trimmed.toLowerCase(Locale.US).endsWith(".webp")
                || trimmed.toLowerCase(Locale.US).endsWith(".jpg")) {
            return RuleIconSource.asset(trimmed);
        }
        return RuleIconSource.drawableResource(trimmed);
    }
}
