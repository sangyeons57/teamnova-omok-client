package com.example.application.usecase;

import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.core.exception.UseCaseException;
import com.example.application.dto.command.CreateAccountCommand;
import com.example.application.port.out.IdentifyRepository;
import com.example.domain.identity.entity.Identity;

import java.util.Objects;

/**
 * Coordinates account creation requests by delegating to the login repository and mapping the result.
 */
public class CreateAccountUseCase extends UseCase<CreateAccountCommand, Identity> {

    private static final String ERROR_CODE = "AUTH_CREATE_ACCOUNT_FAILED";

    private final IdentifyRepository repository;

    public CreateAccountUseCase(UseCaseConfig config, IdentifyRepository repository) {
        super(config);
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    protected Identity run(CreateAccountCommand command) throws UseCaseException {
        Objects.requireNonNull(command, "command");
        Identity identity = repository.createAccount(command.getProvider(), command.getProviderUserId());
        if (identity == null) {
            throw UseCaseException.of(ERROR_CODE, "No identity session was produced");
        }
        return identity;
    }

    private String resolveMessage(Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Failed to complete guest sign-up";
        }
        return message;
    }
}
