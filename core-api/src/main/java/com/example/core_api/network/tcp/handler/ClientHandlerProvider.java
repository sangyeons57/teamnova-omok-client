package com.example.core_api.network.tcp.handler;

import java.util.Objects;
import java.util.function.Supplier;

public interface ClientHandlerProvider {
    ClientFrameHandler acquire();

    static ClientHandlerProvider singleton(ClientFrameHandler handler) {
        Objects.requireNonNull(handler, "handler");
        return () -> handler;
    }

    static ClientHandlerProvider factory(Supplier<ClientFrameHandler> supplier) {
        Objects.requireNonNull(supplier, "supplier");
        return supplier::get;
    }
}
