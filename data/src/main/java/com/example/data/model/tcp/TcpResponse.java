package com.example.data.model.tcp;

import com.example.core_api.network.tcp.protocol.Frame;
import com.example.core_api.network.tcp.protocol.FrameType;

import java.util.Objects;

public class TcpResponse {

    private final FrameType frameType;
    private final long requestId;
    private final byte[] payload;

    private final Throwable error;

    private TcpResponse (FrameType frameType, long requestId , byte[] payload, Throwable error) {
        this.frameType = frameType;
        this.payload = payload != null ? payload.clone() : new byte[0];
        this.requestId = requestId;

        this.error = error;
    }

    public boolean isSuccess () {
        return error == null;
    }

    public byte[] payload() {
        return payload.clone();
    }
    public FrameType frameType() {
        return frameType;
    }
    public long requestId() {
        return requestId;
    }

    public Throwable error() {
        return error;
    }



    public static TcpResponse from(Frame frame) {
        Objects.requireNonNull(frame, "frame");
        return new TcpResponse(FrameType.lookup(frame.type()), frame.requestId(), frame.payload(), null);
    }

    public static TcpResponse err(Throwable error) {
        return new TcpResponse(null, -1, null, error);
    }
}
