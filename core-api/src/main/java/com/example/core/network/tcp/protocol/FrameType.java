package com.example.core.network.tcp.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enumerates well-known frame types. Extend as new protocol messages are introduced.
 */
public enum FrameType {
    HELLO((byte) 0);

    private static final Map<Byte, FrameType> LOOKUP = new ConcurrentHashMap<>();

    static {
        Arrays.stream(FrameType.values()).forEach(type -> LOOKUP.put(type.code, type));
    }

    private final byte code;

    FrameType(byte code) {
        this.code = code;
    }

    public byte code() {
        return code;
    }

    public static Optional<FrameType> lookup(byte code) {
        return Optional.ofNullable(LOOKUP.get(code));
    }
}
