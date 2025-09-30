package com.example.data.repository.realtime;

import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.core.network.tcp.protocol.FrameType;
import com.example.data.datasource.DefaultTcpServerDataSource;
import com.example.data.exception.TcpRemoteException;
import com.example.data.model.tcp.TcpRequest;
import com.example.data.model.tcp.TcpResponse;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class RealtimeRepositoryImpl implements RealtimeRepository {

    private static final long HELLO_TIMEOUT_SECONDS = 5L;

    private final DefaultTcpServerDataSource tcpServerDataSource;

    public RealtimeRepositoryImpl(DefaultTcpServerDataSource tcpServerDataSource) {
        this.tcpServerDataSource = Objects.requireNonNull(tcpServerDataSource, "tcpServerDataSource");
    }

    @Override
    public CompletableFuture<String> hello(String payload) {
        byte[] requestPayload = payload != null
                ? payload.getBytes(StandardCharsets.UTF_8)
                : new byte[0];

        TcpRequest request = TcpRequest.of(FrameType.HELLO, requestPayload, Duration.ofSeconds(HELLO_TIMEOUT_SECONDS));
        CompletableFuture<TcpResponse> responseFuture = tcpServerDataSource.execute(request);

        return responseFuture.thenApply(response -> {
            if (!response.isSuccess()) {
                Throwable error = response.error();
                if (error instanceof RuntimeException runtime) {
                    throw runtime;
                }
                throw new TcpRemoteException("HELLO request failed", error);
            }
            return new String(response.payload(), StandardCharsets.UTF_8);
        });
    }
}
