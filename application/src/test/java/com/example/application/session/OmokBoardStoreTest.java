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
    public void removeStone_clearsPlacement() {
        boardStore.initializeBoard(15, 15);
        boardStore.applyStone(new OmokStonePlacement(1, 2, OmokStoneType.BLACK));

        boardStore.removeStone(1, 2);

        OmokBoardState boardState = boardStore.getBoardStateStream().getValue();
        assertTrue(boardState.getPlacements().isEmpty());
    }

    @Test
    public void clearBoard_resetsToEmpty() {
        boardStore.initializeBoard(10, 10);
        boardStore.applyStone(new OmokStonePlacement(0, 0, OmokStoneType.BLACK));

        boardStore.clearBoard();

        OmokBoardState boardState = boardStore.getBoardStateStream().getValue();
        assertTrue(boardState.getPlacements().isEmpty());
    }
}
