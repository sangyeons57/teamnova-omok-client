package com.example.core_di.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.application.session.GameInfoStore;
import com.example.application.session.GameSessionInfo;
import com.example.application.session.MatchState;
import com.example.core_api.network.tcp.dispatcher.ClientDispatchResult;
import com.example.core_api.network.tcp.protocol.Frame;
import com.example.core_api.network.tcp.protocol.FrameType;
import com.example.core_di.tcp.JoinInGameSessionHandler.Logger;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * Unit tests for {@link JoinInGameSessionHandler}.
 */
public class JoinInGameSessionHandlerTest {

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private TrackingGameInfoStore gameInfoStore;
    private JoinInGameSessionHandler handler;
    private TestLogger logger;

    @Before
    public void setUp() {
        gameInfoStore = new TrackingGameInfoStore();
        logger = new TestLogger();
        handler = new JoinInGameSessionHandler(gameInfoStore, logger);
    }

    @Test
    public void handle_validPayload_updatesStore() {
        String json = "{"
                + "\"sessionId\":\"session-123\","
                + "\"createdAt\":1700000000,"
                + "\"users\":["
                + "{\"userId\":\"alice\",\"displayName\":\"Alice\",\"profileIconCode\":2},"
                + "{\"userId\":\"bob\",\"displayName\":\"\", \"profileIconCode\":5}"
                + "]"
                + "}";
        Frame frame = new Frame(FrameType.JOIN_IN_GAME_SESSION, 0L, json.getBytes(StandardCharsets.UTF_8));

        ClientDispatchResult result = handler.handle(null, frame);

        assertSame(ClientDispatchResult.continueDispatch(), result);
        assertNull("no warnings expected but got: " + logger.getLastWarning(), logger.getLastWarning());
        assertNull("no errors expected but got: " + logger.getLastError(), logger.getLastError());
        assertNotNull("game session should be stored", gameInfoStore.lastSession);
        assertEquals("session-123", gameInfoStore.lastSession.getSessionId());
        assertEquals(1700000000L, gameInfoStore.lastSession.getCreatedAt());
        assertEquals(2, gameInfoStore.lastSession.getParticipants().size());
        assertEquals(MatchState.MATCHED, gameInfoStore.lastMatchState);
    }

    @Test
    public void handle_invalidPayload_doesNotUpdateStore() {
        String json = "{invalid";
        Frame frame = new Frame(FrameType.JOIN_IN_GAME_SESSION, 0L, json.getBytes(StandardCharsets.UTF_8));

        handler.handle(null, frame);

        assertNotNull("error should be logged", logger.getLastError());
        assertNull("game session should not be stored", gameInfoStore.lastSession);
        assertNull("match state should not change", gameInfoStore.lastMatchState);
    }


    private static final class TrackingGameInfoStore extends GameInfoStore {
        private GameSessionInfo lastSession;
        private MatchState lastMatchState;

        @Override
        public void updateGameSession(@NonNull GameSessionInfo session) {
            super.updateGameSession(session);
            lastSession = session;
        }

        @Override
        public void updateMatchState(@NonNull MatchState state) {
            super.updateMatchState(state);
            lastMatchState = state;
        }
    }

    private static final class TestLogger implements Logger {
        private String lastWarning;
        private String lastError;
        private Throwable lastThrowable;

        @Override
        public void warn(@NonNull String message) {
            lastWarning = message;
        }

        @Override
        public void error(@NonNull String message, @NonNull Throwable throwable) {
            lastError = message + ": " + throwable.getClass().getSimpleName()
                    + " - " + throwable.getMessage();
            lastThrowable = throwable;
        }

        String getLastWarning() {
            return lastWarning;
        }

        String getLastError() {
            return lastError;
        }

        Throwable getLastThrowable() {
            return lastThrowable;
        }
    }
}
