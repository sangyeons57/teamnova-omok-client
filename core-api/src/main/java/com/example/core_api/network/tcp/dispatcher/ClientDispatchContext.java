package com.example.core_api.network.tcp.dispatcher;

import com.example.core_api.network.tcp.TcpClient;
import com.example.core_api.network.tcp.handler.ClientHandlerProvider;
import com.example.core_api.network.tcp.protocol.Frame;

/**
 * Provides contextual information to dispatch effects emitted by handlers.
 */
public record ClientDispatchContext(TcpClient client,
                                    Frame frame,
                                    ClientDispatcher dispatcher,
                                    int frameType,
                                    ClientHandlerProvider provider) {
}
