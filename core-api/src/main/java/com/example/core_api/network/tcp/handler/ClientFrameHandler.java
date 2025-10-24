package com.example.core_api.network.tcp.handler;

import com.example.core_api.network.tcp.TcpClient;
import com.example.core_api.network.tcp.dispatcher.ClientDispatchResult;
import com.example.core_api.network.tcp.protocol.Frame;

@FunctionalInterface
public interface ClientFrameHandler {
    ClientDispatchResult handle(TcpClient client, Frame frame) throws Exception;
}
