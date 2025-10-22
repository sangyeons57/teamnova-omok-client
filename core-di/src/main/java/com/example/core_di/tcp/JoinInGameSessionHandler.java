package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.GameParticipantInfo;
import com.example.application.session.GameSessionInfo;
import com.example.application.session.MatchState;
import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.dispatcher.ClientDispatchResult;
import com.example.core.network.tcp.handler.ClientFrameHandler;
import com.example.core.network.tcp.protocol.Frame;
import com.example.core.network.tcp.protocol.FrameType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Handles JOIN_IN_GAME_SESSION frames and updates shared game session state.
 */
public final class JoinInGameSessionHandler extends AbstractJsonFrameHandler {
    private final GameInfoStore gameInfoStore;
    private final Logger logger;
    private static final String TAG = "JoinInGameSessionHandler";

    public JoinInGameSessionHandler(@NonNull GameInfoStore gameInfoStore) {
        this(gameInfoStore, new AndroidLogger());
    }

    public JoinInGameSessionHandler(@NonNull GameInfoStore gameInfoStore,
                                    @NonNull Logger logger) {
        super(TAG, FrameType.JOIN_IN_GAME_SESSION.name());
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
        this.logger = Objects.requireNonNull(logger, "logger");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        long createdAt = root.optLong("createdAt", 0L);
        JSONArray usersArray = root.optJSONArray("users");
        Map<String, GameParticipantInfo> participants = new HashMap<>();
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
                Log.d(TAG, "User joined session → userId=" + userId
                        + ", displayName=" + displayName
                        + ", profileIconCode");
                participants.put(userId, new GameParticipantInfo(userId, displayName, profileIconCode));
            }
        }

        GameSessionInfo sessionInfo = new GameSessionInfo(sessionId, createdAt, participants);
        gameInfoStore.updateGameSession(sessionInfo);
        gameInfoStore.updateMatchState(MatchState.MATCHED);
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
