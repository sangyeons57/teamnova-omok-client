package com.example.core.network.tcp.handler;

import java.util.Objects;
import java.util.function.Consumer;
import com.example.core.network.tcp.dispatcher.ClientDispatcher;

public interface ClientHandlerRegistry {
    void configure(ClientDispatcher dispatcher);

    static ClientHandlerRegistry empty() {
        return dispatcher -> { };
    }

    static ClientHandlerRegistry of(Consumer<ClientDispatcher> consumer) {
        Objects.requireNonNull(consumer, "consumer");
        return consumer::accept;
    }

    static ClientHandlerRegistry compose(ClientHandlerRegistry... registries) {
        Objects.requireNonNull(registries, "registries");
        return dispatcher -> {
            for (ClientHandlerRegistry registry : registries) {
                if (registry == null) {
                    continue;
                }
                registry.configure(dispatcher);
            }
        };
    }
}
