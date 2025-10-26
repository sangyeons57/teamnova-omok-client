package com.example.application.port.in;

import java.util.function.Supplier;

public final class UseCaseProviders {
    public static <T extends UseCase<?, ?>> UseCaseProvider<T> singleton(Supplier<T> creator) {
        return new UseCaseProvider<T>() {
            private volatile T instance;
            @Override public T get() {
                T v = instance;
                if(v == null) {
                    synchronized(this) {
                        if(instance == null) {
                            instance = v = creator.get();
                        }
                        v = instance;
                    }
                }
                return v;
            }
        };
    }
    public static <T extends UseCase<?, ?>> UseCaseProvider<T> factory(Supplier<T> creator) {
        return creator::get;
    }
}
