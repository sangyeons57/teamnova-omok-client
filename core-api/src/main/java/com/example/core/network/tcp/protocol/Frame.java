package com.example.core.network.tcp.protocol;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable representation of a single frame used by the Omok server protocol.
 */
public final class Frame {
    private final FrameType frameType;
    private final long requestId;
    private final byte[] payload;

    public Frame(byte type, long requestId, byte[] payload) {
        this.frameType = FrameType.lookup(type);
        this.requestId = requestId & 0xFFFF_FFFFL;
        this.payload = Objects.requireNonNull(payload, "payload");
    }

    public Frame(FrameType type, long requestId, byte[] payload) {
        this(Objects.requireNonNull(type, "type").code(), requestId, payload);
    }

    public byte type() {
        return frameType.code();
    }

    public Optional<FrameType> frameType() {
        return Optional.ofNullable(frameType);
    }

    public long requestId() {
        return requestId;
    }

    public byte[] payload() {
        return payload;
    }

    public int payloadLength() {
        return payload.length;
    }

    public Frame withPayloadCopy() {
        return new Frame(frameType.code(), requestId, Arrays.copyOf(payload, payload.length));
    }

    @Override
    public String toString() {
        return "Frame{" +
                "type=" + frameType().map(Enum::name).orElseGet(() -> Integer.toString(frameType.code() & 0xFF)) +
                ", requestId=" + requestId +
                ", payloadLength=" + payload.length +
                '}';
    }
}
