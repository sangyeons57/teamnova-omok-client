package com.example.application.port.in;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UseCaseRegistry {
    private final Map<Class<? extends UseCase<?, ?>>, UseCaseProvider<? extends UseCase<?, ?>>> map = new ConcurrentHashMap<>();

    public <T extends UseCase<?, ?>> void register(Class<T> key, UseCaseProvider<T> provider) {
        map.put(key, provider);
    }

    @SuppressWarnings("unchecked")
    public <T extends UseCase<?, ?>> T get(Class<T> key) {
        UseCaseProvider<? extends UseCase<?, ?>> provider = map.get(key);
        if (provider == null) {
            throw new IllegalArgumentException("No provider for " + key.getName());
        }
        return (T) provider.get();
    }
}
