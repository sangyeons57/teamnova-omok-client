package com.example.application.usecase;

import com.example.application.dto.response.SelfDataResponse;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.UserRepository;
import com.example.core.exception.UseCaseException;

public class SelfDataUseCase extends UseCase<UseCase.None, SelfDataResponse> {

    private static final String ERROR_CODE = "SELF_DATA_FAILED";

    private final UserRepository userRepository;

    public SelfDataUseCase(UseCaseConfig useCaseConfig, UserRepository userRepository) {
        super(useCaseConfig);
        this.userRepository = userRepository;
    }

    @Override
    protected SelfDataResponse run(None input) throws UseCaseException {
        try {
            return new SelfDataResponse(userRepository.fetchSelfData());
        } catch (UseCaseException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw UseCaseException.of(ERROR_CODE, resolveMessage(exception, "Failed to load self data"));
        }
    }

    private String resolveMessage(RuntimeException exception, String fallback) {
        String message = exception.getMessage();
        return (message == null || message.trim().isEmpty()) ? fallback : message;
    }
}
