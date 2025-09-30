package com.example.core.network.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import com.example.core.network.tcp.protocol.Frame;
import com.example.core.network.tcp.protocol.FrameType;

/**
 * Contract for clients capable of exchanging Omok TCP frames with the server.
 */
public interface TcpClient extends Closeable {

    void connect() throws IOException;

    boolean isConnected();

    CompletableFuture<Frame> send(byte type, byte[] payload) throws IOException;

    default CompletableFuture<Frame> send(FrameType type, byte[] payload) throws IOException {
        if (type == null) {
            throw new NullPointerException("type");
        }
        return send(type.code(), payload);
    }

    void sendAndForget(byte type, byte[] payload) throws IOException;

    default void sendAndForget(FrameType type, byte[] payload) throws IOException {
        if (type == null) {
            throw new NullPointerException("type");
        }
        sendAndForget(type.code(), payload);
    }

    @Override
    void close();
}
