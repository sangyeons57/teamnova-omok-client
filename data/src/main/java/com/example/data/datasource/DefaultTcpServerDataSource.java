package com.example.data.datasource;

import android.util.Log;

import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.protocol.Frame;
import com.example.data.exception.TcpRemoteException;
import com.example.data.model.tcp.TcpRequest;
import com.example.data.model.tcp.TcpResponse;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class DefaultTcpServerDataSource {

    private static final String TAG = "DefaultTcpServerDataSource";

    private final TcpClient tcpClient;

    public DefaultTcpServerDataSource(TcpClient tcpClient) {
        this.tcpClient = Objects.requireNonNull(tcpClient, "tcpClient");
    }

    public CompletableFuture<TcpResponse> execute(TcpRequest request) {
        Objects.requireNonNull(request, "request");
        try {

            return tcpClient.send(request.frameType(), request.payload(), request.timeoutSeconds()).handle((frame, ex) -> {
                if (ex != null) {
                    return TcpResponse.err(ex);
                } else {
                    return TcpResponse.from(frame);
                }
            });
        } catch (IOException e) {
            Log.e(TAG, request.frameType().name() + " request failed", e);
            return CompletableFuture.completedFuture(TcpResponse.err(new TcpRemoteException( request.frameType().name() + " request failed" )));
        }
    }
}
