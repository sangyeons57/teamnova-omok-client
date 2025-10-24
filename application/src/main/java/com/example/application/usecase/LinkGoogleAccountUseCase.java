package com.example.application.usecase;

import com.example.application.dto.command.LinkGoogleAccountCommand;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.IdentifyRepository;
import com.example.application.session.UserSessionStore;
import com.example.application.wrapper.UserSession;
import com.example.core_api.exception.UseCaseException;

/**
 * Links the current session with a Google provider account using the supplied ID token.
 */
public class LinkGoogleAccountUseCase extends UseCase<LinkGoogleAccountCommand, UseCase.None> {

    private static final String ERROR_CODE = "LINK_GOOGLE_FAILED";
    private static final String SESSION_ERROR_CODE = "SESSION_NOT_FOUND";
    private static final String SESSION_ERROR_MESSAGE = "No active user session";
    private static final String FALLBACK_MESSAGE = "구글 계정 연동에 실패했습니다.";

    private final IdentifyRepository identifyRepository;
    private final UserSessionStore userSessionStore;

    public LinkGoogleAccountUseCase(UseCaseConfig useCaseConfig,
                                    IdentifyRepository identifyRepository,
                                    UserSessionStore userSessionStore) {
        super(useCaseConfig);
        this.identifyRepository = identifyRepository;
        this.userSessionStore = userSessionStore;
    }

    @Override
    protected None run(LinkGoogleAccountCommand input) throws UseCaseException {
        UserSession current = userSessionStore.getCurrentSession();
        if (current == null) {
            throw UseCaseException.of(SESSION_ERROR_CODE, SESSION_ERROR_MESSAGE);
        }

        try {
            UserSession linked = identifyRepository.linkGoogleAccount(input.providerIdToken());
            userSessionStore.update(linked);
            return None.INSTANCE;
        } catch (RuntimeException exception) {
            throw UseCaseException.of(ERROR_CODE, resolveMessage(exception));
        }
    }

    private String resolveMessage(RuntimeException exception) {
        String message = exception.getMessage();
        if (message == null || message.trim().isEmpty()) {
            return FALLBACK_MESSAGE;
        }
        return message;
    }
}
