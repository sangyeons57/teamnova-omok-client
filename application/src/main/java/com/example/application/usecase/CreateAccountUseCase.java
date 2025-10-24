package com.example.application.usecase;

import com.example.application.dto.response.CreateAccountResponse;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.wrapper.GetOrCreateResult;
import com.example.core_api.exception.UseCaseException;
import com.example.application.dto.command.CreateAccountCommand;
import com.example.application.port.out.user.IdentifyRepository;
import com.example.domain.user.entity.User;

import java.util.Objects;

/**
 * Coordinates account creation requests by delegating to the login repository and mapping the result.
 */
public class CreateAccountUseCase extends UseCase<CreateAccountCommand, CreateAccountResponse> {

    private static final String ERROR_CODE = "AUTH_CREATE_ACCOUNT_FAILED";

    private final IdentifyRepository repository;

    public CreateAccountUseCase(UseCaseConfig config, IdentifyRepository repository) {
        super(config);
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    protected CreateAccountResponse run(CreateAccountCommand command) throws UseCaseException {
        Objects.requireNonNull(command, "command");
        GetOrCreateResult<User> identity = repository.createAccount(command.getProvider(), command.getGoogleIdToken());
        if (identity == null) {
            throw UseCaseException.of(ERROR_CODE, "No identity session was produced");
        }
        return new CreateAccountResponse( ((GetOrCreateResult.Ok<User>) identity).value(), ((GetOrCreateResult.Ok<User>) identity).isNew() );
    }

    private String resolveMessage(Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return "Failed to complete guest sign-up";
        }
        return message;
    }
}
