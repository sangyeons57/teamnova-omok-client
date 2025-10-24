package com.example.data.model.tcp;

import com.example.core_api.network.tcp.protocol.FrameType;

import java.time.Duration;
import java.util.Objects;

public class TcpRequest {
    private final FrameType frameType;
    private final byte[] payload;
    private final Duration timeout;

    private TcpRequest (FrameType frameType, byte[] payload, Duration duration) {
        this.frameType = Objects.requireNonNull(frameType, "frameType");
        this.payload = payload != null ? payload.clone() : new byte[0];
        this.timeout = duration;
    }

    public byte[] payload() {
        return payload.clone();
    }
    public FrameType frameType() {
        return frameType;
    }
    public Duration timeoutSeconds() {
        return timeout;
    }



    public static TcpRequest of(FrameType frameType, byte[] payload, Duration timeout) {
        return new TcpRequest(frameType, payload, timeout);
    }

    public static TcpRequest of(FrameType frameType, byte[] payload) {
        return new TcpRequest(frameType, payload, Duration.ZERO);
    }

    public static TcpRequest of(FrameType frameType) {
        return new TcpRequest(frameType, new byte[0], Duration.ZERO);
    }
}
