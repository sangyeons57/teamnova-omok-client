package com.example.data.repository.realtime;

import android.util.Log;

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

    private static final String TAG = "RealtimeRepositoryImpl";
    private static final long HELLO_TIMEOUT_SECONDS = 5L;
    private static final long AUTH_TIMEOUT_SECONDS = 5L;
    private static final long PLACE_STONE_TIMEOUT_SECONDS = 5L;

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

    @Override
    public CompletableFuture<Boolean> auth(String accessToken) {
        byte[] requestPayload = accessToken != null
                ? accessToken.getBytes(StandardCharsets.UTF_8)
                : new byte[0];

        TcpRequest request = TcpRequest.of(FrameType.AUTH, requestPayload, Duration.ofSeconds(AUTH_TIMEOUT_SECONDS));
        CompletableFuture<TcpResponse> responseFuture = tcpServerDataSource.execute(request);

        return responseFuture.thenApply(response -> {
            if (!response.isSuccess()) {
                Throwable error = response.error();
                if (error instanceof RuntimeException runtime) {
                    throw runtime;
                }
                throw new TcpRemoteException("AUTH request failed", error);
            }
            String payload = new String(response.payload(), StandardCharsets.UTF_8).trim();
            return "1".equals(payload);
        });
    }

    @Override
    public void joinMatch(String match) {
        byte[] requestPayload = match != null
                ? match.getBytes(StandardCharsets.UTF_8)
                : new byte[0];

        TcpRequest request = TcpRequest.of(FrameType.JOIN_MATCH, requestPayload, Duration.ofSeconds(10));
        CompletableFuture<TcpResponse> responseFuture = tcpServerDataSource.execute(request);
        responseFuture.thenApply(response -> {
            if (response.isSuccess()) {
                String payload = new String(response.payload(), StandardCharsets.UTF_8).trim();
                Log.d(TAG, "JoinMatch success: " + payload);
            } else {
                Log.e(TAG, "JoinMatch failed: " + response.error() + " ");
            }
            return null;
        });
    }

    @Override
    public void readyInGameSession() {
        TcpRequest request = TcpRequest.of(FrameType.READY_IN_GAME_SESSION, new byte[0], Duration.ofSeconds(5));
        CompletableFuture<TcpResponse> responseFuture = tcpServerDataSource.execute(request);
        responseFuture.thenApply(response -> {
            if (response.isSuccess()) {
                String payload = new String(response.payload(), StandardCharsets.UTF_8).trim();
                Log.d(TAG, "ReadyInGameSession success: " + payload);
            } else {
                Log.e(TAG, "ReadyInGameSession failed: " + response.error());
            }
            return null;
        });
    }

    @Override
    public void placeStone(int x, int y) {
        String payloadString = x + "," + y;
        byte[] payload = payloadString.getBytes(StandardCharsets.UTF_8);
        TcpRequest request = TcpRequest.of(FrameType.PLACE_STONE, payload, Duration.ofSeconds(PLACE_STONE_TIMEOUT_SECONDS));
        CompletableFuture<TcpResponse> responseFuture = tcpServerDataSource.execute(request);
        responseFuture.whenComplete((response, throwable) -> {
            if (throwable != null) {
                Log.e(TAG, "PlaceStone request failed for coords=(" + x + "," + y + ")", throwable);
                return;
            }
            if (response.isSuccess()) {
                String ackPayload = new String(response.payload(), StandardCharsets.UTF_8).trim();
                Log.d(TAG, "PlaceStone success: " + ackPayload);
            } else {
                Log.e(TAG, "PlaceStone failed: " + response.error());
            }
        });
    }

}
