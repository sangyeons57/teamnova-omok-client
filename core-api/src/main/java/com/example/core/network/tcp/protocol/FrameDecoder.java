package com.example.core.network.tcp.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.core.network.tcp.protocol.ProtocolConstants.HEADER_LENGTH;
import static com.example.core.network.tcp.protocol.ProtocolConstants.LENGTH_FIELD_SIZE;
import static com.example.core.network.tcp.protocol.ProtocolConstants.MAX_FRAME_SIZE;
import static com.example.core.network.tcp.protocol.ProtocolConstants.TYPE_FIELD_SIZE;

/**
 * Stateful decoder that turns bytes from the socket into {@link Frame} instances.
 */
public final class FrameDecoder {
    private byte[] buffer = new byte[4096];
    private int buffered;

    public List<Frame> feed(byte[] data, int length) throws FrameDecodingException {
        Objects.requireNonNull(data, "data");
        if (length < 0 || length > data.length) {
            throw new IllegalArgumentException("length out of bounds");
        }
        if (length == 0) {
            return List.of();
        }
        ensureCapacity(buffered + length);
        System.arraycopy(data, 0, buffer, buffered, length);
        buffered += length;
        return extractFrames();
    }

    private List<Frame> extractFrames() throws FrameDecodingException {
        List<Frame> frames = new ArrayList<>();
        int offset = 0;
        while (buffered - offset >= HEADER_LENGTH) {
            int totalLength = readInt(buffer, offset);
            if (totalLength < HEADER_LENGTH) {
                throw new FrameDecodingException("Frame length " + totalLength + " smaller than header");
            }
            if (totalLength > MAX_FRAME_SIZE) {
                throw new FrameDecodingException("Frame length " + totalLength + " exceeds maximum " + MAX_FRAME_SIZE);
            }
            if (buffered - offset < totalLength) {
                break;
            }
            byte type = buffer[offset + LENGTH_FIELD_SIZE];
            long requestId = readUnsignedInt(buffer, offset + LENGTH_FIELD_SIZE + TYPE_FIELD_SIZE);
            int payloadLength = totalLength - HEADER_LENGTH;
            byte[] payload = new byte[payloadLength];
            if (payloadLength > 0) {
                System.arraycopy(buffer, offset + HEADER_LENGTH, payload, 0, payloadLength);
            }
            frames.add(new Frame(type, requestId, payload));
            offset += totalLength;
        }
        if (offset > 0) {
            int remaining = buffered - offset;
            if (remaining > 0) {
                System.arraycopy(buffer, offset, buffer, 0, remaining);
            }
            buffered = remaining;
        }
        return frames;
    }

    private static int readInt(byte[] source, int offset) {
        return ((source[offset] & 0xFF) << 24)
                | ((source[offset + 1] & 0xFF) << 16)
                | ((source[offset + 2] & 0xFF) << 8)
                | (source[offset + 3] & 0xFF);
    }

    private static long readUnsignedInt(byte[] source, int offset) {
        return readInt(source, offset) & 0xFFFF_FFFFL;
    }

    private void ensureCapacity(int required) {
        if (required <= buffer.length) {
            return;
        }
        int newCapacity = buffer.length;
        while (newCapacity < required) {
            newCapacity <<= 1;
        }
        byte[] next = new byte[newCapacity];
        System.arraycopy(buffer, 0, next, 0, buffered);
        buffer = next;
    }
}
