package com.example.core_di.tcp.handler;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.core_api.network.tcp.TcpClient;
import com.example.core_api.network.tcp.dispatcher.ClientDispatchResult;
import com.example.core_api.network.tcp.handler.ClientFrameHandler;
import com.example.core_api.network.tcp.protocol.Frame;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Base handler that converts incoming frame payloads into JSON objects.
 */
abstract class AbstractJsonFrameHandler implements ClientFrameHandler {

    private final String tag;
    private final String frameName;

    AbstractJsonFrameHandler(@NonNull String tag, @NonNull String frameName) {
        this.tag = Objects.requireNonNull(tag, "tag");
        this.frameName = Objects.requireNonNull(frameName, "frameName");
    }

    @Override
    public final ClientDispatchResult handle(TcpClient client, Frame frame) {
        if (frame == null) {
            Log.w(tag, frameName + " frame is null");
            return ClientDispatchResult.continueDispatch();
        }
        byte[] payload = frame.payload();
        if (payload == null || payload.length == 0) {
            Log.w(tag, frameName + " payload missing");
            return ClientDispatchResult.continueDispatch();
        }

        String raw = new String(payload, StandardCharsets.UTF_8);
        Log.d(tag, frameName + " payload: " + raw);

        try {
            JSONObject root = new JSONObject(raw);
            onJsonPayload(root);
        } catch (JSONException e) {
            Log.e(tag, "Failed to parse " + frameName + " payload", e);
        } catch (Exception e) {
            Log.e(tag, "Unexpected error handling " + frameName + " frame", e);
        }
        return ClientDispatchResult.continueDispatch();
    }

    protected abstract void onJsonPayload(@NonNull JSONObject root);
}
