package com.example.application.dto.command;

import java.util.Objects;

public record UserDataCommand(String userId) {

    public UserDataCommand {
        Objects.requireNonNull(userId, "userId");
    }
}

