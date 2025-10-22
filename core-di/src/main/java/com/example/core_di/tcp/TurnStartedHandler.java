package com.example.core_di.tcp;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import com.example.application.session.OmokStonePlacement;
import com.example.application.session.OmokStoneType;
import com.example.core.network.tcp.protocol.FrameType;

import org.json.JSONObject;


public class TurnStartedHandler extends AbstractJsonFrameHandler{
    private static final String TAG = "TurnStartedHandler";

    private GameInfoStore gameInfoStore;

    public TurnStartedHandler(GameInfoStore gameInfoStore) {
        this();
        this.gameInfoStore = gameInfoStore;
    }

    TurnStartedHandler() {
        super(TAG, FrameType.TURN_STARTED.name());
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {
        String sessionId = root.optString("sessionId");
        JSONObject turnJson = root.optJSONObject("turn");
        TurnPayloadProcessor.applyTurn(gameInfoStore, turnJson, TAG);
    }

}
