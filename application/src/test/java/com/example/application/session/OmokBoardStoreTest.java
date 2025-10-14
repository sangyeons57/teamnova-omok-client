package com.example.application.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class OmokBoardStoreTest {

    @Rule
    public final InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private OmokBoardStore boardStore;

    @Before
    public void setUp() {
        boardStore = new OmokBoardStore();
    }

    @Test
    public void initializeBoard_setsEmptyGrid() {
        boardStore.initializeBoard(19, 19);

        OmokBoardState state = boardStore.getBoardStateStream().getValue();
        assertEquals(19, state.getWidth());
        assertEquals(19, state.getHeight());
        assertTrue(state.getPlacements().isEmpty());
    }

    @Test
    public void applyStone_replacesExistingCoordinate() {
        boardStore.initializeBoard(19, 19);

        boardStore.applyStone(new OmokStonePlacement(3, 4, OmokStoneType.BLACK));
        boardStore.applyStone(new OmokStonePlacement(3, 4, OmokStoneType.WHITE));

        OmokBoardState boardState = boardStore.getBoardStateStream().getValue();
        assertEquals(1, boardState.getPlacements().size());
        assertEquals(OmokStoneType.WHITE, boardState.getPlacements().get(0).getStoneType());
    }

    @Test
    public void clearBoard_resetsToEmpty() {
        boardStore.initializeBoard(10, 10);
        boardStore.applyStone(new OmokStonePlacement(0, 0, OmokStoneType.BLACK));

        boardStore.clearBoard();

        OmokBoardState boardState = boardStore.getBoardStateStream().getValue();
        assertTrue(boardState.getPlacements().isEmpty());
    }

    @Test
    public void updateBoardSnapshot_replacesEntireBoard() {
        boardStore.initializeBoard(2, 2);

        OmokStoneType[] cells = new OmokStoneType[]{
                OmokStoneType.RED,
                OmokStoneType.EMPTY,
                OmokStoneType.UNKNOWN,
                OmokStoneType.BLUE,
                OmokStoneType.JOKER,
                OmokStoneType.BLOCKER
        };

        boardStore.updateBoardSnapshot(3, 2, cells);

        OmokBoardState boardState = boardStore.getBoardStateStream().getValue();
        assertEquals(3, boardState.getWidth());
        assertEquals(2, boardState.getHeight());
        assertEquals(OmokStoneType.RED, boardState.getStone(0, 0));
        assertEquals(OmokStoneType.BLOCKER, boardState.getStone(2, 1));
    }
}
