package com.example.core.client.handler;

import com.example.core.client.protocol.Frame;
import com.example.core.client.transport.FramedTcpClient;

@FunctionalInterface
public interface ClientFrameHandler {
    void handle(FramedTcpClient client, Frame frame) throws Exception;
}
