package com.example.feature_home.home.presentation.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines the supported rule codes in presentation order.
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
    HUNTING_CLOTH,
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
