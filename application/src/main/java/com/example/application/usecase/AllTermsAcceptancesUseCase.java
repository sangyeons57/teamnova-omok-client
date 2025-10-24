package com.example.application.usecase;

import com.example.application.dto.command.AcceptTermsCommand;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.core_api.exception.UseCaseException;
import com.example.application.port.out.user.TermsRepository;

import java.util.Objects;

/**
 * Coordinates requests for accepting all mandatory terms.
 */
public class AllTermsAcceptancesUseCase extends UseCase<AcceptTermsCommand, UseCase.None> {

    private static final String ERROR_CODE = "TERMS_ACCEPTANCE_FAILED";

    private final TermsRepository repository;

    public AllTermsAcceptancesUseCase(UseCaseConfig config, TermsRepository repository) {
        super(config);
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    protected UseCase.None run(AcceptTermsCommand command) throws UseCaseException {
        Objects.requireNonNull(command, "command");
        try {
            repository.acceptTerms(command.getAccessToken(), command.getAcceptTypes());
            return UseCase.None.INSTANCE;
        } catch (RuntimeException exception) {
            throw UseCaseException.of(ERROR_CODE, resolveMessage(exception));
        }
    }

    private String resolveMessage(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Failed to accept terms";
        }
        return message;
    }
}
