package com.example.application.usecase;

import com.example.application.dto.command.ChangeNameCommand;
import com.example.application.port.in.UseCase;
import com.example.application.port.in.UseCaseConfig;
import com.example.application.port.out.user.UserRepository;
import com.example.core.exception.UseCaseException;
import com.example.domain.user.value.UserDisplayName;

public class ChangeNameUseCase extends UseCase<ChangeNameCommand, UseCase.None> {

    private final UserRepository userRepository;

    public ChangeNameUseCase(UseCaseConfig useCaseConfig, UserRepository userRepository) {
        super(useCaseConfig);
        this.userRepository = userRepository;
    }

    @Override
    protected None run(ChangeNameCommand input) throws UseCaseException {
        userRepository.changeName(UserDisplayName.of(input.newName()));
        return None.INSTANCE;
    }
}
