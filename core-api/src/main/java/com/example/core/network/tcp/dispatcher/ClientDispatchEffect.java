package com.example.core.network.tcp.dispatcher;

@FunctionalInterface
public interface ClientDispatchEffect {
    void apply(ClientDispatchContext context) throws Exception;
}
