package com.example.core_di.tcp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.util.Base64;

import com.example.application.session.OmokBoardState;
import com.example.application.session.OmokBoardStore;
import com.example.application.session.OmokStoneType;
import com.example.core_di.tcp.processor.BoardPayloadProcessor;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

public class BoardPayloadProcessorTest {

    private OmokBoardStore boardStore;

    @Before
    public void setUp() {
        boardStore = new OmokBoardStore();
    }

    @Test
    public void applyBoardSnapshot_decodesBase64Board() throws Exception {
        byte[] cells = new byte[]{-1, 0, 3, 5};
        String encoded = Base64.encodeToString(cells, Base64.NO_WRAP);

        JSONObject boardJson = new JSONObject()
                .put("width", 2)
                .put("height", 2)
                .put("cells", encoded);

        boolean applied = BoardPayloadProcessor.applyBoardSnapshot(boardJson, boardStore, "TestTag");

        assertTrue(applied);
        OmokBoardState state = boardStore.getBoardStateStream().getValue();
        assertEquals(2, state.getWidth());
        assertEquals(2, state.getHeight());
        assertEquals(OmokStoneType.EMPTY, state.getStone(0, 0));
        assertEquals(OmokStoneType.RED, state.getStone(1, 0));
        assertEquals(OmokStoneType.GREEN, state.getStone(0, 1));
        assertEquals(OmokStoneType.BLOCKER, state.getStone(1, 1));
    }
}
