package com.example.application.usecase;

import com.example.application.dto.response.SelfDataResponse;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.UserRepository;
import com.example.application.session.UserSessionStore;
import com.example.domain.user.entity.User;
import com.example.domain.user.factory.UserFactory;
import com.example.core.exception.UseCaseException;

public class SelfDataUseCase extends UseCase<UseCase.None, SelfDataResponse> {

    private static final String ERROR_CODE = "SELF_DATA_FAILED";

    private final UserRepository userRepository;
    private final UserSessionStore userSessionStore;

    public SelfDataUseCase(UseCaseConfig useCaseConfig, UserRepository userRepository, UserSessionStore userSessionStore) {
        super(useCaseConfig);
        this.userRepository = userRepository;
        this.userSessionStore = userSessionStore;
    }

    @Override
    protected SelfDataResponse run(None input) throws UseCaseException {
        try {
            User fetched = userRepository.fetchSelfData();
            User existing = userSessionStore.getCurrentUser();
            User merged = existing != null
                    ? UserFactory.mergeProfile(existing, fetched)
                    : fetched;
            userSessionStore.updateUser(merged);
            return new SelfDataResponse(merged);
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
