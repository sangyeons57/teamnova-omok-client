package com.example.domain.rules;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Enumerates the supported rule codes. The order matches presentation requirements.
 */
public enum RuleCode {
    STONE_CONVERSION,
    SPEED_GAME,
    BLOCKER_SUMMON,
    JOKER_SUMMON,
    GO_CAPTURE,
    SEQUENTIAL_CONVERSION,
    REVERSI_CONVERSION,
    SIX_IN_ROW,
    BLACK_VIEW,
    RANDOM_MOVE,
    INFECTION,
    NEW_PLAYER,
    PROTECTIVE_ZONE,
    MIRROR_BOARD,
    BLOCKER_BAN,
    JOKER_PROMOTION,
    LUCKY_SEVEN,
    TEN_CHAIN_ELIMINATION,
    AIM_MISS,
    COLOSSEUM,
    EVOLUTION,
    SPEED_GAME_2,
    DELAYED_REVEAL,
    LOW_DENSITY_PURGE,
    RANDOM_PLACEMENT,
    TURN_ORDER_SHUFFLE,
    ROUND_TRIP_TURNS;

    @NonNull
    public String getValue() {
        return name();
    }

    /**
     * Resolves the enum from a persisted code value.
     */
    @NonNull
    public static RuleCode of(@NonNull String value) {
        Objects.requireNonNull(value, "value == null");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("RuleCode value must not be empty");
        }
        normalized = normalized.replace('-', '_').replace(' ', '_');
        try {
            return RuleCode.valueOf(normalized);
        } catch (IllegalArgumentException notUppercase) {
            return RuleCode.valueOf(normalized.toUpperCase(Locale.US));
        }
    }

    /**
     * Returns the codes in presentation order.
     */
    @NonNull
    public static List<String> orderedValues() {
        RuleCode[] codes = values();
        List<String> result = new ArrayList<>(codes.length);
        for (RuleCode code : codes) {
            result.add(code.getValue());
        }
        return Collections.unmodifiableList(result);
    }
}
