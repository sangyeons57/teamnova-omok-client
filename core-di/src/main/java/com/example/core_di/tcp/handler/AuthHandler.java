package com.example.core_di.tcp.handler;

import android.util.Log;

import com.example.core_api.network.tcp.TcpClient;
import com.example.core_api.network.tcp.dispatcher.ClientDispatchResult;
import com.example.core_api.network.tcp.handler.ClientFrameHandler;
import com.example.core_api.network.tcp.protocol.Frame;

import java.nio.charset.StandardCharsets;

public class AuthHandler implements ClientFrameHandler {
    private static final String TAG = AuthHandler.class.getSimpleName();
    @Override
    public ClientDispatchResult handle(TcpClient client, Frame frame) throws Exception {
        String raw = new String(frame.payload(), StandardCharsets.UTF_8);
        if (raw.equals("0")) {
            Log.d(TAG, "Authentication failed");
        } else if (raw.equals("1")) {
            Log.d(TAG, "Authentication succeeded");
        } else if (raw.equals("2")) {
            Log.d(TAG, "Authentication reconnected");

        }
        return null;
    }
}
