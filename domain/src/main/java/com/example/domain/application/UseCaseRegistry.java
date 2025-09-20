package com.example.domain.application;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class UseCaseRegistry {
    private final Map<Class<?>, UseCaseProvider<?>> map = new ConcurrentHashMap<>();

    public <T> void register(Class<T> key, UseCaseProvider<T> provider) { map.put(key, provider); }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> key) {
        UseCaseProvider<?> p = map.get(key);
        if (p == null) throw new IllegalArgumentException("No provider for " + key.getName());
        return (T) p.get();
    }
}

