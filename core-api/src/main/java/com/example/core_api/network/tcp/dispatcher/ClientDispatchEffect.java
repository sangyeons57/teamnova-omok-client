package com.example.core_api.network.tcp.dispatcher;

@FunctionalInterface
public interface ClientDispatchEffect {
    void apply(ClientDispatchContext context) throws Exception;
}
