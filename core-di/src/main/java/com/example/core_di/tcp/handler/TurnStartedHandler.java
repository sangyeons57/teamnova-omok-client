package com.example.core_di.tcp.handler;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.core_api.network.tcp.protocol.FrameType;
import com.example.core_di.tcp.processor.TurnPayloadProcessor;

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
