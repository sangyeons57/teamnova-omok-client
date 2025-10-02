package com.example.core.network.tcp.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enumerates well-known frame types. Extend as new protocol messages are introduced.
 */
public enum FrameType {
    NONE((byte) -1),
    HELLO((byte) 0),
    AUTH((byte) 1),
    PING((byte) 2),
    JOIN_MATCH((byte) 3),
    JOIN_IN_GAME_SESSION((byte) 4),
    LEAVE_IN_GAME_SESSION((byte) 5),
    ;

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

    public static FrameType lookup(byte code) {
        return LOOKUP.get(code) == null ? NONE : LOOKUP.get(code) ;
    }
}
