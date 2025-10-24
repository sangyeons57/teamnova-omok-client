package com.example.application.usecase;

import com.example.application.dto.command.UserDataCommand;
import com.example.application.dto.response.UserDataResponse;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.UserRepository;
import com.example.core_api.exception.UseCaseException;
import com.example.domain.user.value.UserId;

public class UserDataUseCase extends UseCase<UserDataCommand, UserDataResponse> {

    private static final String ERROR_CODE = "USER_DATA_FAILED";

    private final UserRepository userRepository;

    public UserDataUseCase(UseCaseConfig useCaseConfig, UserRepository userRepository) {
        super(useCaseConfig);
        this.userRepository = userRepository;
    }

    @Override
    protected UserDataResponse run(UserDataCommand input) throws UseCaseException {
        try {
            return new UserDataResponse(userRepository.fetchUserData(UserId.of(input.userId())));
        } catch (UseCaseException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw UseCaseException.of(ERROR_CODE, resolveMessage(exception, "Failed to load user data"));
        }
    }

    private String resolveMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return (message == null || message.trim().isEmpty()) ? fallback : message;
    }
}
