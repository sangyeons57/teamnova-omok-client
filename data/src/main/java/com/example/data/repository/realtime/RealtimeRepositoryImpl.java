package com.example.data.repository.realtime;

import com.example.application.port.out.realtime.PostGameDecisionOption;
import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.core_api.network.tcp.protocol.FrameType;
import com.example.data.datasource.DefaultTcpServerDataSource;
import com.example.data.model.tcp.TcpRequest;
import com.example.data.repository.realtime.codec.PlaceStoneMessageCodec;
import com.example.data.repository.realtime.codec.PostGameDecisionMessageCodec;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

public final class RealtimeRepositoryImpl implements RealtimeRepository {

    private static final long AUTH_TIMEOUT_SECONDS = 5L;
    private static final long PLACE_STONE_TIMEOUT_SECONDS = 5L;
    private static final long POST_GAME_DECISION_TIMEOUT_SECONDS = 5L;

    private final DefaultTcpServerDataSource tcpServerDataSource;

    public RealtimeRepositoryImpl(DefaultTcpServerDataSource tcpServerDataSource) {
        this.tcpServerDataSource = Objects.requireNonNull(tcpServerDataSource, "tcpServerDataSource");
    }

    @Override
    public void auth(String accessToken) {
        byte[] requestPayload = accessToken != null
                ? accessToken.getBytes(StandardCharsets.UTF_8)
                : new byte[0];

        TcpRequest request = TcpRequest.of(FrameType.AUTH, requestPayload, Duration.ofSeconds(AUTH_TIMEOUT_SECONDS));
        tcpServerDataSource.send(request);
    }

    @Override
    public void reconnectTcp(String accessToken, String gameSessionId) {
        String combineText = accessToken + ":" + gameSessionId;
        byte[] requestPayload = combineText.getBytes(StandardCharsets.UTF_8);

        TcpRequest request = TcpRequest.of(FrameType.RECONNECTING, requestPayload, Duration.ofSeconds(5));
        tcpServerDataSource.send(request);
    }

    @Override
    public void joinMatch(String match) {
        byte[] requestPayload = match != null
                ? match.getBytes(StandardCharsets.UTF_8)
                : new byte[0];

        TcpRequest request = TcpRequest.of(FrameType.JOIN_MATCH, requestPayload, Duration.ofSeconds(10));
        tcpServerDataSource.send(request);
    }

    @Override
    public void leaveMatch() {
        TcpRequest request = TcpRequest.of(FrameType.LEAVE_MATCH, new byte[0], Duration.ofSeconds(5));
        tcpServerDataSource.send(request);
    }


    @Override
    public void readyInGameSession() {
        TcpRequest request = TcpRequest.of(FrameType.READY_IN_GAME_SESSION, new byte[0], Duration.ofSeconds(5));
        tcpServerDataSource.send(request);
    }

    @Override
    public void placeStone(int x, int y) {
        byte[] payload = PlaceStoneMessageCodec.encode(x, y);
        TcpRequest request = TcpRequest.of(FrameType.PLACE_STONE, payload, Duration.ofSeconds(PLACE_STONE_TIMEOUT_SECONDS));
        tcpServerDataSource.send(request);
    }

    @Override
    public void postGameDecision(PostGameDecisionOption decision) {
        Objects.requireNonNull(decision, "decision");
        byte[] payload = PostGameDecisionMessageCodec.encode(decision);
        TcpRequest request = TcpRequest.of(FrameType.POST_GAME_DECISION, payload, Duration.ofSeconds(POST_GAME_DECISION_TIMEOUT_SECONDS));
        tcpServerDataSource.send(request);
    }

}
