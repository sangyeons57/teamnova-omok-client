package com.example.application.usecase;

import com.example.application.dto.command.ChangeNameCommand;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.UserRepository;
import com.example.core.exception.UseCaseException;
import com.example.domain.user.value.UserDisplayName;

public class ChangeNameUseCase extends UseCase<ChangeNameCommand, UseCase.None> {

    private static final String ERROR_CODE = "CHANGE_NAME_FAILED";
    private static final String FALLBACK_MESSAGE = "Failed to change display name";

    private final UserRepository userRepository;

    public ChangeNameUseCase(UseCaseConfig useCaseConfig, UserRepository userRepository) {
        super(useCaseConfig);
        this.userRepository = userRepository;
    }

    @Override
    protected None run(ChangeNameCommand input) throws UseCaseException {
        String errorMessage = userRepository.changeName(UserDisplayName.of(input.newName()));
        if (errorMessage != null) {
            throw UseCaseException.of(ERROR_CODE, normalize(errorMessage));
        }
        return None.INSTANCE;
    }

    private String normalize(String message) {
        if (message == null || message.trim().isEmpty()) {
            return FALLBACK_MESSAGE;
        }
        return message;
    }
}
