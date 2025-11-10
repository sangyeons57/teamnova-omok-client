package com.example.core_di.tcp.handler;

import androidx.annotation.NonNull;

import com.example.application.session.GameInfoStore;
import com.example.application.session.OmokBoardStore;
import com.example.core_api.network.tcp.protocol.FrameType;

import org.json.JSONObject;

public class ReconnectingHandler extends AbstractJsonFrameHandler {
    private static final String TAG = "ReconnectingHandler";

    private final GameInfoStore gameInfoStore;

    public ReconnectingHandler (GameInfoStore gameInfoStore) {
        super(TAG, FrameType.RECONNECTING.name());
        this.gameInfoStore = gameInfoStore;
    }

    @Override
    protected void onJsonPayload(@NonNull JSONObject root) {

    }
}
