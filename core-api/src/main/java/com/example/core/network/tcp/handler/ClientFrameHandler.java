package com.example.core.network.tcp.handler;

import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.dispatcher.ClientDispatchResult;
import com.example.core.network.tcp.protocol.Frame;

@FunctionalInterface
public interface ClientFrameHandler {
    ClientDispatchResult handle(TcpClient client, Frame frame) throws Exception;
}
