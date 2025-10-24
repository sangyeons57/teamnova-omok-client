package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import com.example.application.session.OmokStonePlacement;
import com.example.application.session.OmokStoneType;
import com.example.application.session.TurnEndEvent;
import com.example.core_api.network.model.TurnEndCause;
import com.example.core_api.network.model.TurnEndStatus;
import com.example.core_api.network.tcp.protocol.FrameType;

import org.json.JSONObject;
import java.util.Objects;

public class TurnEndedHandler extends AbstractJsonFrameHandler{
    private static final String TAG = "TurnEndedHandler";

    private final GameInfoStore gameInfoStore;
    private final OmokBoardStore boardStore;

    public TurnEndedHandler( @NonNull GameInfoStore gameInfoStore) {
        this(gameInfoStore, gameInfoStore.getBoardStore());
    }

    private TurnEndedHandler (GameInfoStore gameInfoStore, OmokBoardStore omokBoardStore) {
        super(TAG, FrameType.TURN_ENDED.name());

        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
        this.boardStore = Objects.requireNonNull(omokBoardStore, "boardStore");
    }


    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        String playerId = root.optString("playerId", "");
        TurnEndCause cause = TurnEndCause.lookup(root.optString("cause", ""));
        TurnEndStatus status = TurnEndStatus.lookup(root.optString("status", ""));
        boolean timedOut = root.optBoolean("timedOut", false);
        JSONObject moveJson = root.optJSONObject("move");
        PlacementedStone placementedStone = null;
        if (moveJson != null) {
            int x = moveJson.optInt("x", -1);
            int y = moveJson.optInt("y", -1);
            String stoneLabel = moveJson.optString("stone", "");
            OmokStoneType stoneType = StoneTypeMapper.fromNetworkLabel(stoneLabel);
            placementedStone = new PlacementedStone(x, y, stoneType);
        }

        // Post TurnEndEvent to GameInfoStore
        gameInfoStore.postTurnEndEvent(new TurnEndEvent(sessionId, cause, status, timedOut));

        if (cause == TurnEndCause.MOVE && placementedStone != null) {
            moveStone(placementedStone);
        } else if (cause == TurnEndCause.TIMEOUT) {
            timeoutProcess(sessionId);
        } else {
            Log.w(TAG, "Unknown turn end cause '" + cause + "'. Ignoring.");
        }
    }

    protected void moveStone(PlacementedStone placementedStone) {
        int x = placementedStone.x;
        int y = placementedStone.y;

        OmokStoneType stoneType = placementedStone.omokStoneType;
        Log.i(TAG, "Stone placed → "
                + ", stone=" + stoneType
                + ", coord=(" + x + "," + y + ")");

        if (stoneType.isPlaced() && x >= 0 && y >= 0) {
            try {
                boardStore.applyStone(new OmokStonePlacement(x, y, stoneType));
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                Log.e(TAG, "Stone coordinate outside board bounds (" + x + "," + y + ")", e);
            }
        } else {
            Log.w(TAG, "Skipping board update due to invalid stone or coordinates.");
        }
    }


    protected void timeoutProcess(String sessionId) {
        Log.i(TAG, "Turn timeout → sessionId=" + sessionId);

    }

    public static class PlacementedStone {
        public final int x;
        public final int y;
        public final OmokStoneType omokStoneType;
        public PlacementedStone(int x, int y, OmokStoneType omokStoneType) {
            this.x = x;
            this.y = y;
            this.omokStoneType = omokStoneType;
        }
    }
}
