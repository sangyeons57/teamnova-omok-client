package com.example.core.network.tcp.protocol;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enumerates well-known frame types. Extend as new protocol messages are introduced.
 */
public enum FrameType {
    HELLO((byte) 0),
    AUTH((byte) 1),
    PING((byte) 2),
    JOIN_MATCH((byte) 3),
    JOIN_IN_GAME_SESSION((byte) 4),
    LEAVE_IN_GAME_SESSION((byte) 5),
    READY_IN_GAME_SESSION((byte)6),
    GAME_SESSION_STARTED((byte)7),
    PLACE_STONE((byte)8),
    TURN_STARTED((byte)9),
    TURN_ENDED((byte)10),
    GAME_SESSION_COMPLETED((byte)11),
    GAME_POST_DECISION_PROMPT((byte)12),
    POST_GAME_DECISION((byte)13),
    GAME_POST_DECISION_UPDATE((byte)14),
    GAME_SESSION_REMATCH_STARTED((byte)15),
    GAME_SESSION_TERMINATED((byte)16),
    GAME_SESSION_PLAYER_DISCONNECTED((byte)17),
    BOARD_UPDATED((byte)18),
    LEAVE_MATCH((byte)19),

    ERROR((byte)255)
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
        return LOOKUP.get(code) == null ? ERROR : LOOKUP.get(code) ;
    }
}
