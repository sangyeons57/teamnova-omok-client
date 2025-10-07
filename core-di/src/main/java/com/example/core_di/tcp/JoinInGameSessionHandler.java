package com.example.core_di.tcp;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.GameParticipantInfo;
import com.example.application.session.GameSessionInfo;
import com.example.application.session.MatchState;
import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.dispatcher.ClientDispatchResult;
import com.example.core.network.tcp.handler.ClientFrameHandler;
import com.example.core.network.tcp.protocol.Frame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles JOIN_IN_GAME_SESSION frames and updates shared game session state.
 */
public final class JoinInGameSessionHandler implements ClientFrameHandler {
    private final GameInfoStore gameInfoStore;
    private final Logger logger;

    public JoinInGameSessionHandler(@NonNull GameInfoStore gameInfoStore) {
        this(gameInfoStore, new AndroidLogger());
    }

    public JoinInGameSessionHandler(@NonNull GameInfoStore gameInfoStore,
                                    @NonNull Logger logger) {
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    @Override
    public ClientDispatchResult handle(TcpClient client, Frame frame) {
        if (frame == null) {
            logger.warn("JOIN_IN_GAME_SESSION frame is null");
            return ClientDispatchResult.continueDispatch();
        }

        byte[] payload = frame.payload();
        if (payload == null || payload.length == 0) {
            logger.warn("JOIN_IN_GAME_SESSION payload empty");
            return ClientDispatchResult.continueDispatch();
        }

        try {
            GameSessionInfo sessionInfo = parsePayload(payload);
            gameInfoStore.updateGameSession(sessionInfo);
            gameInfoStore.updateMatchState(MatchState.MATCHED);
        } catch (JSONException e) {
            logger.error("Failed to parse JOIN_IN_GAME_SESSION payload", e);
        } catch (Exception e) {
            logger.error("Unexpected error handling JOIN_IN_GAME_SESSION", e);
        }

        return ClientDispatchResult.continueDispatch();
    }

    GameSessionInfo parsePayload(@NonNull byte[] payload) throws JSONException {
        JSONObject root = new JSONObject(new String(payload, StandardCharsets.UTF_8));
        String sessionId = root.optString("sessionId", "");
        long createdAt = root.optLong("createdAt", 0L);
        JSONArray usersArray = root.optJSONArray("users");
        List<GameParticipantInfo> participants = new ArrayList<>();
        if (usersArray != null) {
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObject = usersArray.optJSONObject(i);
                if (userObject == null) {
                    continue;
                }
                String userId = userObject.optString("userId", "");
                String displayName = userObject.optString("displayName", userId);
                if (displayName == null || displayName.trim().isEmpty()) {
                    displayName = userId;
                }
                int profileIconCode = userObject.optInt("profileIconCode", 0);
                participants.add(new GameParticipantInfo(userId, displayName, profileIconCode));
            }
        }
        return new GameSessionInfo(sessionId, createdAt, participants);
    }

    public interface Logger {
        void warn(@NonNull String message);

        void error(@NonNull String message, @NonNull Throwable throwable);
    }

    private static final class AndroidLogger implements Logger {
        @Override
        public void warn(@NonNull String message) {
            android.util.Log.w("JoinInGameSessionHandler", message);
        }

        @Override
        public void error(@NonNull String message, @NonNull Throwable throwable) {
            android.util.Log.e("JoinInGameSessionHandler", message, throwable);
        }
    }
}
