package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import com.example.application.session.OmokStonePlacement;
import com.example.application.session.OmokStoneType;
import com.example.core.network.tcp.protocol.FrameType;
import com.example.core.sound.SoundManager;
import com.example.core_di.R;

import org.json.JSONObject;

import java.util.Objects;

public class TurnEndedHandler extends AbstractJsonFrameHandler{
    private static final String TAG = "TurnEndedHandler";
    private static final String SOUND_ID_PLACE_STONE = "game_place_stone";

    private final GameInfoStore gameInfoStore;
    private final OmokBoardStore boardStore;
    private final SoundManager soundManager;
    private volatile boolean soundRegistered = false;

    public TurnEndedHandler( @NonNull GameInfoStore gameInfoStore, @NonNull SoundManager soundManager) {
        this(gameInfoStore, gameInfoStore.getBoardStore() , soundManager);
    }

    private TurnEndedHandler (GameInfoStore gameInfoStore, OmokBoardStore omokBoardStore, SoundManager soundManager) {
        super(TAG, FrameType.TURN_ENDED.name());

        this.gameInfoStore = Objects.requireNonNull(gameInfoStore, "gameInfoStore");
        this.boardStore = Objects.requireNonNull(omokBoardStore, "boardStore");
        this.soundManager = Objects.requireNonNull(soundManager, "soundManager");
    }


    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId", "");
        String playerId = root.optString("playerId", "");
        TurnEndCause cause = TurnEndCause.lookup(root.optString("cause", ""));
        TurnEndStatus status = TurnEndStatus.lookup(root.optString("status", ""));
        boolean timedOut = root.optBoolean("timedOut", false);
        JSONObject moveJson = root.optJSONObject("move");
        Move move = null;
        if (moveJson != null) {
            int x = moveJson.optInt("x", -1);
            int y = moveJson.optInt("y", -1);
            String stoneLabel = moveJson.optString("stone", "");
            OmokStoneType stoneType = StoneTypeMapper.fromNetworkLabel(stoneLabel);
            move = new Move(x, y, stoneType);
        }

        if (cause == TurnEndCause.MOVE && move != null) {
            moveStone(move);
        } else if (cause == TurnEndCause.TIMEOUT) {
            timeoutProcess(sessionId);
        } else {
            Log.w(TAG, "Unknown turn end cause '" + cause + "'. Ignoring.");
        }
    }

    protected void moveStone(Move move) {
        ensureSoundRegistered();
        int x = move.x;
        int y = move.y;

        OmokStoneType stoneType = move.omokStoneType;
        Log.i(TAG, "Stone placed → "
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

    protected void timeoutProcess(String sessionId) {
        Log.i(TAG, "Turn timeout → sessionId=" + sessionId);

    }

    public static class Move {
        public final int x;
        public final int y;
        public final OmokStoneType omokStoneType;
        public Move(int x, int y, OmokStoneType omokStoneType) {
            this.x = x;
            this.y = y;
            this.omokStoneType = omokStoneType;
        }
    }

    public static enum TurnEndStatus {
        UNKNOWN("UNKNOWN"),

        TIMEOUT("TIMEOUT"),

        SUCCESS("SUCCESS"),
        INVALID_PLAYER("INVALID_PLAYER"),
        INVALID_TURN("INVALID_TURN"),
        GAME_NOT_STARTED("GAME_NOT_STARTED"),
        GAME_NOT_FOUND("GAME_NOT_FOUND"),
        OUT_OF_TURN("OUT_OF_TURN"),
        OUT_OF_BOUNDS("OUT_OF_BOUNDS"),
        INVALID_MOVE("INVALID_MOVE"),
        CELL_OCCUPIED("CELL_OCCUPIED"),
        GAME_FINISHED("GAME_FINISHED"),
        RESTRICTED_ZONE("RESTRICTED_ZONE");

        public final String name;

        TurnEndStatus(String name) {
            this.name = name;
        }
        public static TurnEndStatus lookup(String name) {
            switch (name) {
                case "TIMEOUT":
                    return TIMEOUT;
                case "SUCCESS":
                    return SUCCESS;
                case "INVALID_PLAYER":
                    return INVALID_PLAYER;
                case "INVALID_TURN":
                    return INVALID_TURN;
                case "GAME_NOT_STARTED":
                    return GAME_NOT_STARTED;
                case "GAME_NOT_FOUND":
                    return GAME_NOT_FOUND;
                case "OUT_OF_TURN":
                    return OUT_OF_TURN;
                case "OUT_OF_BOUNDS":
                    return OUT_OF_BOUNDS;
                case "INVALID_MOVE":
                    return INVALID_MOVE;
                case "CELL_OCCUPIED":
                    return CELL_OCCUPIED;
                case "GAME_FINISHED":
                    return GAME_FINISHED;
                case "RESTRICTED_ZONE":
                    return RESTRICTED_ZONE;
                default:
                    return UNKNOWN;
            }
        }
    }


    public static enum TurnEndCause {
        MOVE("MOVE"),
        TIMEOUT("TIMEOUT"),
        UNKNOWN("UNKNOWN");

        public final String name;

        TurnEndCause(String name) {
            this.name = name;
        }
        public static TurnEndCause lookup(String name) {
            switch (name) {
                case "MOVE":
                    return MOVE;
                case "TIMEOUT":
                    return TIMEOUT;
                default:
                    return UNKNOWN;
            }
        }
    }
}
