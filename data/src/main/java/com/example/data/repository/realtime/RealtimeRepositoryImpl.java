package com.example.data.repository.realtime;

import android.util.Log;

import com.example.application.port.out.realtime.PlaceStoneResponse;
import com.example.application.port.out.realtime.PostGameDecisionAck;
import com.example.application.port.out.realtime.PostGameDecisionOption;
import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.core_api.network.tcp.protocol.FrameType;
import com.example.data.datasource.DefaultTcpServerDataSource;
import com.example.data.exception.TcpRemoteException;
import com.example.data.model.tcp.TcpRequest;
import com.example.data.model.tcp.TcpResponse;
import com.example.data.repository.realtime.codec.PlaceStoneMessageCodec;
import com.example.data.repository.realtime.codec.PostGameDecisionMessageCodec;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public final class RealtimeRepositoryImpl implements RealtimeRepository {

    private static final String TAG = "RealtimeRepositoryImpl";
    private static final long HELLO_TIMEOUT_SECONDS = 5L;
    private static final long AUTH_TIMEOUT_SECONDS = 5L;
    private static final long PLACE_STONE_TIMEOUT_SECONDS = 5L;
    private static final long POST_GAME_DECISION_TIMEOUT_SECONDS = 5L;

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
        executeAndLog(request, "JoinMatch",
                payload -> new String(payload, StandardCharsets.UTF_8).trim());
    }

    @Override
    public void leaveMatch() {
        TcpRequest request = TcpRequest.of(FrameType.LEAVE_MATCH, new byte[0], Duration.ofSeconds(5));
        executeAndLog(request, "LeaveMatch",
                payload -> new String(payload, StandardCharsets.UTF_8).trim());
    }

    @Override
    public void readyInGameSession() {
        TcpRequest request = TcpRequest.of(FrameType.READY_IN_GAME_SESSION, new byte[0], Duration.ofSeconds(5));
        executeAndLog(request, "ReadyInGameSession",
                payload -> new String(payload, StandardCharsets.UTF_8).trim());
    }

    @Override
    public CompletableFuture<PlaceStoneResponse> placeStone(int x, int y) {
        byte[] payload = PlaceStoneMessageCodec.encode(x, y);
        TcpRequest request = TcpRequest.of(FrameType.PLACE_STONE, payload, Duration.ofSeconds(PLACE_STONE_TIMEOUT_SECONDS));
        String operation = "PlaceStone(" + x + "," + y + ")";
        CompletableFuture<TcpResponse> responseFuture = tcpServerDataSource.execute(request);
        return responseFuture.thenApply(response -> handlePlaceStoneResponse(operation, response));
    }

    @Override
    public CompletableFuture<PostGameDecisionAck> postGameDecision(PostGameDecisionOption decision) {
        Objects.requireNonNull(decision, "decision");
        byte[] payload = PostGameDecisionMessageCodec.encode(decision);
        TcpRequest request = TcpRequest.of(FrameType.POST_GAME_DECISION, payload, Duration.ofSeconds(POST_GAME_DECISION_TIMEOUT_SECONDS));
        String operation = "PostGameDecision(" + decision.name() + ")";
        CompletableFuture<TcpResponse> responseFuture = tcpServerDataSource.execute(request);
        return responseFuture.thenApply(response -> handlePostGameDecisionResponse(operation, response));
    }

    private void executeAndLog(TcpRequest request,
                               String operation,
                               Function<byte[], String> payloadFormatter) {
        CompletableFuture<TcpResponse> responseFuture = tcpServerDataSource.execute(request);
        responseFuture.whenComplete((response, throwable) -> {
            if (throwable != null) {
                Log.e(TAG, operation + " failed", throwable);
                return;
            }
            if (response == null) {
                Log.e(TAG, operation + " failed: null response");
                return;
            }
            if (response.isSuccess()) {
                byte[] payload = response.payload();
                String payloadText = payloadFormatter != null && payload != null
                        ? payloadFormatter.apply(payload)
                        : "";
                if (payloadText == null || payloadText.isEmpty()) {
                    Log.d(TAG, operation + " success");
                } else {
                    Log.d(TAG, operation + " success: " + payloadText);
                }
            } else {
                Throwable error = response.error();
                Log.e(TAG, operation + " failed: " + (error != null ? error : "unknown error"));
            }
        });
    }

    private PlaceStoneResponse handlePlaceStoneResponse(String operation, TcpResponse response) {
        if (response == null) {
            throw new TcpRemoteException(operation + " failed: null response");
        }
        if (!response.isSuccess()) {
            Throwable error = response.error();
            Log.e(TAG, operation + " failed", error);
            throw new TcpRemoteException(operation + " failed", error);
        }
        PlaceStoneResponse decoded = PlaceStoneMessageCodec.decode(response.payload());
        if (decoded.isSuccess()) {
            Log.d(TAG, operation + " success: " + decoded.rawMessage());
        } else {
            Log.w(TAG, operation + " rejected → status=" + decoded.status() + " payload=" + decoded.rawMessage());
        }
        return decoded;
    }

    private PostGameDecisionAck handlePostGameDecisionResponse(String operation, TcpResponse response) {
        if (response == null) {
            throw new TcpRemoteException(operation + " failed: null response");
        }
        if (!response.isSuccess()) {
            Throwable error = response.error();
            Log.e(TAG, operation + " failed", error);
            throw new TcpRemoteException(operation + " failed", error);
        }
        PostGameDecisionAck ack = PostGameDecisionMessageCodec.decodeAck(response.payload());
        if (ack.status() == PostGameDecisionAck.Status.OK) {
            Log.d(TAG, operation + " success → decision=" + ack.decision());
        } else if (ack.status() == PostGameDecisionAck.Status.ERROR) {
            Log.w(TAG, operation + " rejected → reason=" + ack.errorReason()
                    + ", payload=" + ack.rawMessage());
        } else {
            Log.w(TAG, operation + " returned UNKNOWN state: " + ack.rawMessage());
        }
        return ack;
    }

}
