package com.example.application.usecase;

import com.example.application.dto.command.ChangeProfileIconCommand;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.UserRepository;
import com.example.core.exception.UseCaseException;
import com.example.domain.user.value.UserProfileIcon;

public class ChangeProfileIconUseCase extends UseCase<ChangeProfileIconCommand, UseCase.None> {
    private final UserRepository userRepository;
    public ChangeProfileIconUseCase(UseCaseConfig useCaseConfig, UserRepository userRepository) {
        super(useCaseConfig);
        this.userRepository = userRepository;
    }

    @Override
    protected None run(ChangeProfileIconCommand input) throws UseCaseException {
        userRepository.changeProfileIcon(UserProfileIcon.of(input.newIcon()));
        return None.INSTANCE;
    }
}
