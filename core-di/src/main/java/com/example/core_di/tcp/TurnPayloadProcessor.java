package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.session.GameInfoStore;
import com.example.application.session.GameParticipantInfo;
import com.example.application.session.GameSessionInfo;

import org.json.JSONObject;

import java.util.function.LongSupplier;

/**
 * Applies turn payload updates coming from realtime frames.
 */
final class TurnPayloadProcessor {

    private TurnPayloadProcessor() {
        // Utility class
    }

    static void applyTurn(@NonNull GameInfoStore gameInfoStore,
                          @Nullable JSONObject turnJson,
                          @NonNull String tag) {
        applyTurn(gameInfoStore, turnJson, tag, System::currentTimeMillis);
    }

    static void applyTurn(@NonNull GameInfoStore gameInfoStore,
                          @Nullable JSONObject turnJson,
                          @NonNull String tag,
                          @NonNull LongSupplier nowSupplier) {
        if (turnJson == null) {
            Log.d(tag, "Turn payload missing. Clearing local turn state.");
            gameInfoStore.clearTurnState();
            return;
        }
        int turnNumber = turnJson.optInt("number", -1);
        int round = turnJson.optInt("round", -1);
        int position = turnJson.optInt("position", -1);
        int playerIndex = turnJson.optInt("playerIndex", -1);
        String currentPlayerId = turnJson.optString("currentPlayerId", null);
        long startAt = turnJson.optLong("startAt", 0L);
        long endAt = turnJson.optLong("endAt", 0L);
        int remainingSeconds = computeRemainingSeconds(endAt, nowSupplier);

        Log.d(tag, "Turn update #" + turnNumber
                + " currentPlayerId=" + currentPlayerId
                + " round=" + round
                + " position=" + position
                + " playerIndex=" + playerIndex
                + " remainingSeconds=" + remainingSeconds
                + " startAt=" + startAt
                + " endAt=" + endAt);

        if (playerIndex >= 0) {
            gameInfoStore.setTurnIndex(playerIndex, remainingSeconds, round, position);
        } else if (currentPlayerId == null) {
            Log.d(tag, "Turn payload omitted current player. Clearing turn state.");
            gameInfoStore.clearTurnState();
        } else {
            Log.w(tag, "Unable to resolve participant index for currentPlayerId=" + currentPlayerId);
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

    private static int resolveParticipantIndex(@Nullable GameSessionInfo session,
                                               @Nullable String userId) {
        if (session == null || userId == null || userId.trim().isEmpty()) {
            return -1;
        }
        for (int i = 0; i < session.getParticipantCount(); i++) {
            GameParticipantInfo participant = session.getParticipantOrNull(i);
            if (participant != null && userId.equals(participant.getUserId())) {
                return i;
            }
        }
        return -1;
    }
}
