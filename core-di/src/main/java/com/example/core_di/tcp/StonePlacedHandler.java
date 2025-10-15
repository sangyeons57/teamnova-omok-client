package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import com.example.application.session.OmokStonePlacement;
import com.example.application.session.OmokStoneType;
import com.example.core.sound.SoundManager;
import com.example.core_di.R;
import org.json.JSONObject;

import java.util.Objects;

/**
 * Handles STONE_PLACED broadcasts to keep the local board in sync with the server.
 */
public final class StonePlacedHandler extends AbstractJsonFrameHandler {

    private static final String TAG = "StonePlacedHandler";
    private static final String SOUND_ID_PLACE_STONE = "game_place_stone";

    private final GameInfoStore gameInfoStore;
    private final OmokBoardStore boardStore;
    private final SoundManager soundManager;
    private volatile boolean soundRegistered = false;

    public StonePlacedHandler(@NonNull GameInfoStore gameInfoStore,
                              @NonNull SoundManager soundManager) {
        this(gameInfoStore, gameInfoStore.getBoardStore(), soundManager);
    }

    StonePlacedHandler(@NonNull GameInfoStore gameInfoStore,
                       @NonNull OmokBoardStore boardStore,
                       @NonNull SoundManager soundManager) {
        super(TAG, "STONE_PLACED");
        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
        this.boardStore = Objects.requireNonNull(boardStore, "boardStore");
        this.soundManager = Objects.requireNonNull(soundManager, "soundManager");
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        ensureSoundRegistered();
        String sessionId = root.optString("sessionId", "");
        String placedBy = root.optString("placedBy", "");
        int x = root.optInt("x", -1);
        int y = root.optInt("y", -1);
        String stoneLabel = root.optString("stone", "");

        OmokStoneType stoneType = StoneTypeMapper.fromNetworkLabel(stoneLabel);
        if (stoneType == OmokStoneType.UNKNOWN && !stoneLabel.isEmpty()) {
            Log.w(TAG, "Unknown stone label '" + stoneLabel + "'. Treating as UNKNOWN.");
        }
        Log.i(TAG, "Stone placed → sessionId=" + sessionId
                + ", placedBy=" + placedBy
                + ", stone=" + stoneType
                + ", coord=(" + x + "," + y + ")");

        if (stoneType.isPlaced() && x >= 0 && y >= 0) {
            try {
                boardStore.applyStone(new OmokStonePlacement(x, y, stoneType));
                soundManager.play(SOUND_ID_PLACE_STONE);
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                Log.e(TAG, "Stone coordinate outside board bounds (" + x + "," + y + ")", e);
            }
        } else {
            Log.w(TAG, "Skipping board update due to invalid stone or coordinates.");
        }

        JSONObject boardJson = root.optJSONObject("board");
        if (boardJson == null && root.has("width") && root.has("height")) {
            boardJson = root;
        }
        if (boardJson != null) {
            BoardPayloadProcessor.applyBoardSnapshot(boardJson, boardStore, TAG);
        }

        JSONObject turnJson = root.optJSONObject("turn");
        TurnPayloadProcessor.applyTurn(gameInfoStore, turnJson, TAG);
    }

    private void ensureSoundRegistered() {
        if (soundRegistered) {
            return;
        }
        synchronized (this) {
            if (soundRegistered) {
                return;
            }
            if (!soundManager.isRegistered(SOUND_ID_PLACE_STONE)) {
                soundManager.register(new SoundManager.SoundRegistration(
                        SOUND_ID_PLACE_STONE,
                        R.raw.placing_stone_sound_effect,
                        1f,
                        false
                ));
            }
            soundRegistered = true;
        }
    }
}
