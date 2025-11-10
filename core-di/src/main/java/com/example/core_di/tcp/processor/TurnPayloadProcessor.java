package com.example.core_di.tcp.processor;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.session.GameInfoStore;

import org.json.JSONObject;

import java.util.function.LongSupplier;

/**
 * Applies turn payload updates coming from realtime frames.
 */
public final class TurnPayloadProcessor {

    private TurnPayloadProcessor() {
        // Utility class
    }

    public static void applyTurn(@NonNull GameInfoStore gameInfoStore,
                          @Nullable JSONObject turnJson,
                          @NonNull String tag) {
        applyTurn(gameInfoStore, turnJson, tag, System::currentTimeMillis);
    }

    public static void applyTurn(@NonNull GameInfoStore gameInfoStore,
                          @Nullable JSONObject turnJson,
                          @NonNull String tag,
                          @NonNull LongSupplier nowSupplier) {
        if (turnJson == null) {
            Log.d(tag, "Turn payload missing. Clearing local turn state.");
            gameInfoStore.clearTurnState();
            return;
        }
        String currentPlayerId = turnJson.optString("currentPlayerId", null);
        long startAt = turnJson.optLong("startAt", 0L);
        long endAt = turnJson.optLong("endAt", 0L);
        int turnNumber = turnJson.optInt("number", 0);
        int roundNumber = turnJson.optInt("round", 0);
        int positionInRound = turnJson.optInt("position", 0);
        int playerIndex = turnJson.optInt("playerIndex", -1);
        int remainingSeconds = computeRemainingSeconds(endAt, nowSupplier);

        Log.d(tag, "Turn update "
                + " currentPlayerId=" + currentPlayerId
                + " remainingSeconds=" + remainingSeconds
                + " startAt=" + startAt
                + " endAt=" + endAt
                + " turnNumber=" + turnNumber
                + " roundNumber=" + roundNumber
                + " positionInRound=" + positionInRound
                + " playerIndex=" + playerIndex);

        if (currentPlayerId != null) {
            gameInfoStore.setTurnState(currentPlayerId,
                    remainingSeconds,
                    startAt,
                    endAt,
                    turnNumber,
                    roundNumber,
                    positionInRound,
                    playerIndex);
        } else {
            Log.w(tag, "Turn payload omitted current player. Clearing turn state.");
            gameInfoStore.clearTurnState();
        }
    }

    private static int computeRemainingSeconds(long endAtMillis,
                                               @NonNull LongSupplier nowSupplier) {
        if (endAtMillis <= 0L) {
            return 0;
        }
        long now = nowSupplier.getAsLong();
        long remainingMillis = endAtMillis - now;
        if (remainingMillis <= 0L) {
            return 0;
        }
        return (int) ((remainingMillis + 999L) / 1_000L);
    }
}
