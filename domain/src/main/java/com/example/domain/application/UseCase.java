package com.example.domain.application;

/**
 * Base contract for synchronous or asynchronous use cases that return a
 * {@link UResult} wrapper.
 */
public interface UseCase<I, O> {

    UResult<O> execute(I input);

    /** Marker input for use cases that do not require parameters. */
    final class None {
        public static final None INSTANCE = new None();

        private None() {
        }
    }
}
