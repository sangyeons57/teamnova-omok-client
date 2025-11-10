package com.example.core_di.tcp.processor;

import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.session.OmokBoardStore;
import com.example.application.session.OmokStoneType;
import com.example.core_di.tcp.StoneTypeMapper;

import org.json.JSONObject;

public final class BoardPayloadProcessor {

    private static final String KEY_BOARD_WIDTH = "width";
    private static final String KEY_BOARD_HEIGHT = "height";
    private static final String KEY_BOARD_CELLS = "cells";

    private BoardPayloadProcessor() {
    }

    public static boolean applyBoardSnapshot(@Nullable JSONObject boardJson,
                                      @NonNull OmokBoardStore boardStore,
                                      @NonNull String logTag) {
        if (boardJson == null) {
            Log.w(logTag, "Board payload missing");
            return false;
        }
        int width = boardJson.optInt(KEY_BOARD_WIDTH, 0);
        int height = boardJson.optInt(KEY_BOARD_HEIGHT, 0);
        if (width <= 0 || height <= 0) {
            Log.w(logTag, "Board payload has invalid dimensions width=" + width + ", height=" + height);
            return false;
        }
        String cellsBase64 = boardJson.optString(KEY_BOARD_CELLS, "");
        if (cellsBase64.isEmpty()) {
            boardStore.initializeBoard(width, height);
            Log.i(logTag, "Applied empty board snapshot → size=" + width + "x" + height);
            return true;
        }
        byte[] decoded;
        try {
            decoded = Base64.decode(cellsBase64, Base64.DEFAULT);
        } catch (IllegalArgumentException e) {
            Log.e(logTag, "Failed to decode board payload cells from Base64", e);
            return false;
        }

        int expectedCells = Math.max(0, width * height);
        if (decoded.length != expectedCells) {
            Log.w(logTag, "Board cell count mismatch. Expected "
                    + expectedCells + " bytes but received " + decoded.length);
        }

        OmokStoneType[] cells = mapCells(decoded, expectedCells);
        boardStore.updateBoardSnapshot(width, height, cells);
        Log.i(logTag, "Applied board snapshot → size=" + width + "x" + height);
        return true;
    }

    @NonNull
    private static OmokStoneType[] mapCells(@NonNull byte[] decoded, int expectedCells) {
        OmokStoneType[] cells = new OmokStoneType[expectedCells];
        for (int i = 0; i < expectedCells; i++) {
            int rawValue = i < decoded.length ? decoded[i] : -1;
            cells[i] = StoneTypeMapper.fromCellValue(rawValue);
        }
        return cells;
    }
}
