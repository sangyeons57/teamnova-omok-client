package com.example.application.port.in;

import androidx.annotation.NonNull;

/**
 * Exposes access to the shared use case registry so that feature modules can obtain use cases
 * without depending on activity implementations.
 */
public interface UseCaseRegistryProvider {
    @NonNull
    UseCaseRegistry getUseCaseRegistry();
}
