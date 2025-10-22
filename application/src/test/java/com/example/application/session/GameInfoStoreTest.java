package com.example.application.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GameInfoStoreTest {

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private GameInfoStore store;
    private OmokBoardStore boardStore;

    @Before
    public void setUp() {
        boardStore = new OmokBoardStore();
        store = new GameInfoStore(boardStore);
    }

    @Test
    public void updateTurnState_updatesLiveData() {
        GameSessionInfo session = new GameSessionInfo(
                "session",
                1L,
                participants("alice", "bob"));
        store.updateGameSession(session);

        GameTurnState nextTurn = GameTurnState.active(0, 20);
        store.updateTurnState(nextTurn);

        GameTurnState stored = store.getTurnStateStream().getValue();
        assertEquals(nextTurn, stored);
        assertEquals(nextTurn, store.getCurrentTurnState());
        assertEquals("alice", store.getCurrentTurnParticipant().getUserId());
    }

    @Test
    public void updateRemainingSeconds_onlyChangesTimer() {
        GameSessionInfo session = new GameSessionInfo(
                "session",
                1L,
                participants("alice", "bob"));
        store.updateGameSession(session);
        store.updateTurnState(GameTurnState.active(1, 15));

        store.updateRemainingSeconds(7);

        GameTurnState updated = store.getTurnStateStream().getValue();
        assertEquals(7, updated.getRemainingSeconds());
        assertTrue(updated.isActive());
        assertEquals(1, updated.getCurrentIndex());
        assertEquals("bob", store.getCurrentTurnParticipant().getUserId());
    }

    @Test
    public void advanceTurn_cyclesParticipants() {
        GameSessionInfo session = new GameSessionInfo(
                "session",
                1L,
                participants("alice", "bob", "carol"));
        store.updateGameSession(session);
        store.updateTurnState(GameTurnState.active(0, 30));

        store.advanceTurn();
        GameTurnState second = store.getTurnStateStream().getValue();
        assertEquals(1, second.getCurrentIndex());
        assertEquals("bob", store.getCurrentTurnParticipant().getUserId());

        store.advanceTurn();
        GameTurnState third = store.getTurnStateStream().getValue();
        assertEquals(2, third.getCurrentIndex());
        assertEquals("carol", store.getCurrentTurnParticipant().getUserId());

        store.advanceTurn();
        GameTurnState wrapped = store.getTurnStateStream().getValue();
        assertEquals(0, wrapped.getCurrentIndex());
        assertEquals("alice", store.getCurrentTurnParticipant().getUserId());
    }

    @Test
    public void setTurnIndex_clampsWhenOutOfBounds() {
        GameSessionInfo session = new GameSessionInfo(
                "session",
                1L,
                participants("alice", "bob"));
        store.updateGameSession(session);

        GameTurnState state = store.getTurnStateStream().getValue();
        assertEquals(1, state.getCurrentIndex());
        assertEquals(10, state.getRemainingSeconds());
        assertEquals("bob", store.getCurrentTurnParticipant().getUserId());
    }

    @Test
    public void clearGameSession_resetsBoardAndTurn() {
        GameSessionInfo session = new GameSessionInfo(
                "session",
                1L,
                participants("carol", "dave"));
        store.updateGameSession(session);
        store.updateTurnState(GameTurnState.active(1, 30));
        boardStore.initializeBoard(19, 19);
        boardStore.applyStone(new OmokStonePlacement(5, 5, OmokStoneType.BLACK));

        store.clearGameSession();

        assertEquals(GameTurnState.idle(), store.getTurnStateStream().getValue());
        assertTrue(boardStore.getBoardStateStream().getValue().isEmpty());
        assertNull(store.getCurrentTurnParticipant());
    }

    private List<GameParticipantInfo> participants(String... ids) {
        return Arrays.stream(ids)
                .map(id -> new GameParticipantInfo(id, id, 0))
                .collect(Collectors.toList());
    }
}
