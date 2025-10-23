package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import com.example.core.sound.SoundManager;
import com.example.core.sound.SoundIds;

import org.json.JSONObject;

import java.util.Objects;

/**
 * Handles BOARD_UPDATED frames containing the full board snapshot.
 */
public final class BoardUpdatedHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "BoardUpdatedHandler";

    private final OmokBoardStore boardStore;
    private final SoundManager soundManager;

    public BoardUpdatedHandler(@NonNull GameInfoStore gameInfoStore, @NonNull SoundManager soundManager) {
        this(gameInfoStore, gameInfoStore.getBoardStore(), soundManager);
    }

    BoardUpdatedHandler(@NonNull GameInfoStore gameInfoStore,
                        @NonNull OmokBoardStore boardStore,
                        @NonNull SoundManager soundManager) {
        super(TAG, "BOARD_UPDATED");
        this.boardStore = Objects.requireNonNull(boardStore, "boardStore");
        this.soundManager = Objects.requireNonNull(soundManager, "soundManager");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        JSONObject boardJson = extractBoardJson(root);
        boolean applied = BoardPayloadProcessor.applyBoardSnapshot(boardJson, boardStore, TAG);
        if (!applied) {
            Log.w(TAG, "Ignored BOARD_UPDATED payload for sessionId=" + sessionId + " due to invalid board data.");
        } else {
            Log.d(TAG, "Board snapshot synchronized for sessionId=" + sessionId);
            soundManager.play(SoundIds.SOUND_ID_PLACE_STONE);
        }
    }

    private JSONObject extractBoardJson(@NonNull JSONObject root) {
        JSONObject boardJson = root.optJSONObject("board");
        if (boardJson == null && root.has("width") && root.has("height")) {
            boardJson = root;
        }
        return boardJson;
    }
}
