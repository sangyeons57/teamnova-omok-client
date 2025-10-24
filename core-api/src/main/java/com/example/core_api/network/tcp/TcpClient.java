package com.example.core_api.network.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import com.example.core_api.network.tcp.dispatcher.ClientDispatcher;
import com.example.core_api.network.tcp.protocol.Frame;
import com.example.core_api.network.tcp.protocol.FrameType;

/**
 * Contract for clients capable of exchanging Omok TCP frames with the server.
 */
public interface TcpClient extends Closeable {

    void connect() throws IOException;

    boolean isConnected();

    CompletableFuture<Frame> send(byte type, byte[] payload, Duration timeout) throws IOException;

    default CompletableFuture<Frame> send(FrameType type, byte[] payload, Duration timeout) throws IOException {
        if (type == null) {
            throw new NullPointerException("type");
        } else if (payload == null) {
            throw new NullPointerException("payload");
        } else if (timeout == null) {
            timeout = Duration.ofMillis(0);
        }
        return send(type.code(), payload, timeout);
    }

    ClientDispatcher dispatcher();

    @Override
    void close();
}
