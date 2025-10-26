package com.example.application.port.in;

public interface UseCaseProvider<T extends UseCase<?, ?>> {
    T get();
}
