package com.example.data.datasource;

import android.util.Log;

import com.example.core_api.network.tcp.TcpClient;
import com.example.core_api.network.tcp.protocol.Frame;
import com.example.data.exception.TcpRemoteException;
import com.example.data.model.tcp.TcpRequest;
import com.example.data.model.tcp.TcpResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class DefaultTcpServerDataSource {

    private static final String TAG = "DefaultTcpServerDataSource";

    private final TcpClient tcpClient;
    private final ExecutorService ioExecutor;

    public DefaultTcpServerDataSource(TcpClient tcpClient) {
        this.tcpClient = Objects.requireNonNull(tcpClient, "tcpClient");
        this.ioExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "tcp-datasource-io");
            thread.setDaemon(true);
            return thread;
        });
    }

    public CompletableFuture<TcpResponse> execute(TcpRequest request) {
        Objects.requireNonNull(request, "request");
        return CompletableFuture.supplyAsync(() -> performRequest(request), ioExecutor);
    }

    private TcpResponse performRequest(TcpRequest request) {
        try {
            tcpClient.connect();
            Duration timeout = request.timeoutSeconds();
            CompletableFuture<Frame> future = tcpClient.send(request.frameType(), request.payload(), timeout);
            Log.d(TAG, "Sending TCP request. FrameType: " + request.frameType()+"[" + request.frameType().code() + "]" + ", Payload: " + Arrays.toString(request.payload()) + ", Timeout: " + timeout);
            if (timeout == null || timeout.isZero() || timeout.isNegative()) {
                return TcpResponse.from(future.get());
            }
            return TcpResponse.from(future.get(timeout.toMillis(), TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String message = request.frameType().name() + " request interrupted";
            return TcpResponse.err(new TcpRemoteException(message, e));
        } catch (java.util.concurrent.ExecutionException e) {
            Throwable cause = e.getCause() != null ? e.getCause() : e;
            String message = request.frameType().name() + " request failed";
            return TcpResponse.err(new TcpRemoteException(message, cause));
        } catch (TimeoutException e) {
            String message = request.frameType().name() + " request timed out";
            return TcpResponse.err(new TcpRemoteException(message, e));
        } catch (IOException e) {
            Log.e(TAG, request.frameType().name() + " request failed", e);
            String message = "Failed to send " + request.frameType().name() + " request";
            return TcpResponse.err(new TcpRemoteException(message, e));
        } catch (RuntimeException e) {
            String message = request.frameType().name() + " request failed";
            return TcpResponse.err(new TcpRemoteException(message, e));
        }
    }
}
