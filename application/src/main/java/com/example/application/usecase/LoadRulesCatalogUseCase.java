package com.example.application.usecase;

import androidx.annotation.NonNull;
import android.util.Log;

import com.example.application.port.in.UResult;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.rules.RulesRepository;
import com.example.domain.rules.Rule;

import java.util.List;
import java.util.Objects;

/**
 * Use case that exposes the rules catalog for presentation layers.
 */
public final class LoadRulesCatalogUseCase extends UseCase<UseCase.None, List<Rule>> {

    private static final String TAG = "LoadRulesCatalogUseCase";

    @NonNull
    private final RulesRepository rulesRepository;

    public LoadRulesCatalogUseCase(@NonNull UseCaseConfig config,
                                   @NonNull RulesRepository rulesRepository) {
        super(Objects.requireNonNull(config, "config == null"));
        this.rulesRepository = Objects.requireNonNull(rulesRepository, "rulesRepository == null");
    }

    @Override
    protected List<Rule> run(UseCase.None input) {
        List<Rule> rules = rulesRepository.loadRules();
        Log.d(TAG, "Loaded rules count=" + rules.size());
        return rules;
    }

    /**
     * Convenience helper for callers that expect the raw list rather than a {@link UResult}.
     */
    @NonNull
    public UResult<List<Rule>> loadCatalog() {
        return execute(UseCase.None.INSTANCE);
    }
}
