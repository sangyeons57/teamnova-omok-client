package com.example.core_api.network.tcp.protocol;

/**
 * Shared constants for the length-prefixed Omok TCP protocol.
 */
public final class ProtocolConstants {
    public static final int LENGTH_FIELD_SIZE = Integer.BYTES;
    public static final int TYPE_FIELD_SIZE = 1;
    public static final int REQUEST_ID_FIELD_SIZE = Integer.BYTES;
    public static final int HEADER_LENGTH = LENGTH_FIELD_SIZE + TYPE_FIELD_SIZE + REQUEST_ID_FIELD_SIZE;
    public static final int MAX_PAYLOAD_SIZE = 1 << 20; // 1 MiB safety cap
    public static final int MAX_FRAME_SIZE = HEADER_LENGTH + MAX_PAYLOAD_SIZE;

    private ProtocolConstants() {
    }
}
