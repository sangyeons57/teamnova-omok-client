package com.example.core.client.protocol;

import java.nio.ByteBuffer;
import java.util.Objects;

import static com.example.core.client.protocol.ProtocolConstants.HEADER_LENGTH;
import static com.example.core.client.protocol.ProtocolConstants.MAX_PAYLOAD_SIZE;

/**
 * Encodes {@link Frame} instances into network byte order.
 */
public final class FrameEncoder {

    public static byte[] encode(Frame frame) {
        Objects.requireNonNull(frame, "frame");
        int payloadLength = frame.payloadLength();
        if (payloadLength > MAX_PAYLOAD_SIZE) {
            throw new IllegalArgumentException("payload length " + payloadLength + " exceeds maximum " + MAX_PAYLOAD_SIZE);
        }
        int totalLength = HEADER_LENGTH + payloadLength;
        ByteBuffer buffer = ByteBuffer.allocate(totalLength);
        buffer.putInt(totalLength);
        buffer.put(frame.type());
        buffer.putInt((int) frame.requestId());
        buffer.put(frame.payload());
        return buffer.array();
    }
}
