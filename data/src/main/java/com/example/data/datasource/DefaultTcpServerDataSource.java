package com.example.data.datasource;

import android.util.Log;

import com.example.core_api.network.tcp.TcpClient;
import com.example.data.model.tcp.TcpRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

public final class DefaultTcpServerDataSource {

    private static final String TAG = "DefaultTcpServerDataSource";

    private final TcpClient tcpClient;
    private final ExecutorService ioExecutor;

    public DefaultTcpServerDataSource(TcpClient tcpClient) {
        this.tcpClient = Objects.requireNonNull(tcpClient, "tcpClient");
        this.ioExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "tcp-datasource-io");
            thread.setDaemon(true);
            return thread;
        });
    }

    public void send(TcpRequest request) {
        Objects.requireNonNull(request, "request");
        CompletableFuture.runAsync(() -> performSend(request), ioExecutor)
                .exceptionally(throwable -> {
                    Log.e(TAG, request.frameType().name() + " send failed", throwable);
                    return null;
                });
    }

    private void performSend(TcpRequest request) {
        try {
            tcpClient.connect();
            Duration timeout = sanitizeTimeout(request.timeoutSeconds());
            tcpClient.send(request.frameType(), request.payload(), timeout)
                    .exceptionally(throwable -> null); // Ignore expected timeouts for fire-and-forget frames
        } catch (IOException e) {
            Log.e(TAG, "Failed to send " + request.frameType().name() + " request", e);
        }
    }

    private static Duration sanitizeTimeout(Duration timeout) {
        if (timeout == null) {
            return Duration.ZERO;
        }
        if (timeout.isNegative()) {
            return Duration.ZERO;
        }
        return timeout;
    }
}
