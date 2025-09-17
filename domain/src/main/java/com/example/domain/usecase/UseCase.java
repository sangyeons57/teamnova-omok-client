package com.example.domain.usecase;

/**
 * Describes a synchronous unit of domain work.
 */
public interface UseCase<I, O> {

    O execute(I input);

    final class None {
        public static final None INSTANCE = new None();

        private None() {
        }
    }
}
