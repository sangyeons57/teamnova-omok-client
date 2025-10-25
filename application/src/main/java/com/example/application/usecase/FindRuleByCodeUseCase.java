package com.example.application.usecase;

import androidx.annotation.NonNull;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.rules.RulesRepository;
import com.example.core_api.exception.UseCaseException;
import com.example.domain.rules.Rule;
import com.example.domain.rules.RuleCode;

import java.util.Objects;

/**
 * Retrieves rule details by rule code.
 */
public final class FindRuleByCodeUseCase extends UseCase<String, Rule> {

    private static final String ERROR_INVALID_CODE = "RULE_CODE_INVALID";
    private static final String ERROR_NOT_FOUND = "RULE_NOT_FOUND";

    @NonNull
    private final RulesRepository rulesRepository;

    public FindRuleByCodeUseCase(@NonNull UseCaseConfig config,
                                 @NonNull RulesRepository rulesRepository) {
        super(Objects.requireNonNull(config, "config == null"));
        this.rulesRepository = Objects.requireNonNull(rulesRepository, "rulesRepository == null");
    }

    @Override
    protected Rule run(String input) throws UseCaseException {
        String normalizedCode = normalizeCode(input);
        Rule rule = rulesRepository.findRuleByCode(RuleCode.of(normalizedCode));
        if (rule == null) {
            throw UseCaseException.of(ERROR_NOT_FOUND, "규칙 정보를 찾을 수 없습니다: " + normalizedCode);
        }
        return rule;
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
}
