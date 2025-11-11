package com.example.core_di.realtime;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.usecase.TcpAuthUseCase;
import com.example.core_api.event.AppEvent;
import com.example.core_api.event.AppEventBus;
import com.example.core_api.event.TcpAuthRequestedEvent;
import com.example.core_api.event.TcpConnectRequestedEvent;
import com.example.core_api.event.TcpConnectionEvent;
import com.example.core_api.event.TcpConnectionEvent.Status;
import com.example.core_api.network.tcp.TcpClient;
import com.example.core_api.token.TokenStore;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Listens for TCP reconnect/auth events and orchestrates safe reconnect/auth handshakes.
 */
public final class RealtimeReconnectObserver {

    private static final String TAG = "RealtimeReconnectObserver";
    private static final long RETRY_DELAY_MILLIS = 3_000L;

    private final AppEventBus eventBus;
    private final TokenStore tokenStore;
    private final TcpAuthUseCase tcpAuthUseCase;
    private final TcpClient tcpClient;
    private final AtomicBoolean reconnectInFlight = new AtomicBoolean(false);
    private final AtomicBoolean authPending = new AtomicBoolean(true);
    private final ScheduledExecutorService connectExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "tcp-connect");
        t.setDaemon(true);
        return t;
    });
    private final Object connectLock = new Object();
    @Nullable
    private ScheduledFuture<?> pendingConnect;
    private final long listenerId;

    public RealtimeReconnectObserver(@NonNull AppEventBus eventBus,
                                     @NonNull TokenStore tokenStore,
                                     @NonNull TcpAuthUseCase authUseCase,
                                     @NonNull TcpClient tcpClient) {
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
        this.tokenStore = Objects.requireNonNull(tokenStore, "tokenStore");
        this.tcpAuthUseCase = Objects.requireNonNull(authUseCase, "authUseCase");
        this.tcpClient = Objects.requireNonNull(tcpClient, "tcpClient");
        this.listenerId = this.eventBus.register(this::onAppEvent);
    }

    private void onAppEvent(AppEvent event) {
        if (event instanceof TcpConnectionEvent connectionEvent) {
            handleConnectionEvent(connectionEvent);
        } else if (event instanceof TcpAuthRequestedEvent) {
            handleAuthRequestEvent();
        } else if (event instanceof TcpConnectRequestedEvent) {
            handleConnectRequestEvent();
        }
    }

    private void handleConnectionEvent(TcpConnectionEvent connectionEvent) {
        if (connectionEvent.status() == Status.DISCONNECTED) {
            authPending.set(true);
            scheduleConnectAttempt(0L, true);
            return;
        }
        if (connectionEvent.status() == Status.CONNECTED) {
            maybeAuthenticate(true);
        }
    }

    private void handleConnectRequestEvent() {
        authPending.set(true);
        if (tcpClient.isConnected()) {
            maybeAuthenticate(true);
            return;
        }
        scheduleConnectAttempt(0L, true);
    }

    private void handleAuthRequestEvent() {
        authPending.set(true);
        if (!tcpClient.isConnected()) {
            scheduleConnectAttempt(0L, false);
            return;
        }
        maybeAuthenticate(false);
    }

    private void scheduleConnectAttempt(long delayMillis, boolean forceReschedule) {
        synchronized (connectLock) {
            if (pendingConnect != null && !pendingConnect.isDone()) {
                if (!forceReschedule) {
                    return;
                }
                pendingConnect.cancel(true);
            }
            pendingConnect = connectExecutor.schedule(this::attemptConnectOnce, delayMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void attemptConnectOnce() {
        boolean retry = false;
        try {
            if (tcpClient.isConnected()) {
                return;
            }
            tcpClient.connect();
        } catch (IOException e) {
            Log.w(TAG, "TCP connect attempt failed", e);
            retry = true;
        } finally {
            synchronized (connectLock) {
                pendingConnect = null;
            }
        }
        if (retry) {
            scheduleConnectAttempt(RETRY_DELAY_MILLIS, false);
        }
    }

    private void maybeAuthenticate(boolean force) {
        if (!force && !authPending.get()) {
            return;
        }
        triggerAuthHandshake();
    }

    private void triggerAuthHandshake() {
        if (!reconnectInFlight.compareAndSet(false, true)) {
            return;
        }
        try {
            String accessToken = sanitize(tokenStore.getAccessToken());
            if (accessToken == null) {
                return;
            }
            tcpAuthUseCase.execute(accessToken);
            authPending.set(false);
        } finally {
            reconnectInFlight.set(false);
        }
    }

    @Nullable
    private static String sanitize(@Nullable String token) {
        if (token == null) {
            return null;
        }
        String trimmed = token.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public void dispose() {
        eventBus.unregister(listenerId);
        synchronized (connectLock) {
            if (pendingConnect != null) {
                pendingConnect.cancel(true);
                pendingConnect = null;
            }
        }
        connectExecutor.shutdownNow();
    }
}
