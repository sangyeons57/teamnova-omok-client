package com.example.application.port.result;

public sealed interface GetOrCreateResult<T> {
    record Ok<T>(T value, boolean isNew) implements GetOrCreateResult<T> {}
}
