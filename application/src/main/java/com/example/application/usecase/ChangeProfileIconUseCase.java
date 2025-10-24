package com.example.application.usecase;

import com.example.application.dto.command.ChangeProfileIconCommand;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.UserRepository;
import com.example.application.session.UserSessionStore;
import com.example.core_api.exception.UseCaseException;
import com.example.domain.user.entity.User;
import com.example.domain.user.factory.UserFactory;
import com.example.domain.user.value.UserProfileIcon;

public class ChangeProfileIconUseCase extends UseCase<ChangeProfileIconCommand, UseCase.None> {
    private static final String ERROR_CODE = "PROFILE_ICON_CHANGE_FAILED";

    private final UserRepository userRepository;
    private final UserSessionStore userSessionStore;

    public ChangeProfileIconUseCase(UseCaseConfig useCaseConfig,
                                    UserRepository userRepository,
                                    UserSessionStore userSessionStore) {
        super(useCaseConfig);
        this.userRepository = userRepository;
        this.userSessionStore = userSessionStore;
    }

    @Override
    protected None run(ChangeProfileIconCommand input) throws UseCaseException {
        User current = userSessionStore.getCurrentUser();
        if (current == null) {
            throw UseCaseException.of("SESSION_NOT_FOUND", "No active user session");
        }

        boolean success = userRepository.changeProfileIcon(UserProfileIcon.of(input.newIcon()));
        if (!success) {
            throw UseCaseException.of(ERROR_CODE, "Failed to change profile icon");
        }

        User updated = UserFactory.updateProfileIcon(current, input.newIcon());
        userSessionStore.updateUser(updated);
        return None.INSTANCE;
    }
}
