package com.example.core_di.realtime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.application.port.out.realtime.RealtimeRepository;
import com.example.application.session.GameInfoStore;
import com.example.application.session.GameSessionInfo;
import com.example.application.usecase.TcpAuthUseCase;
import com.example.core_api.event.AppEvent;
import com.example.core_api.event.AppEventBus;
import com.example.core_api.event.TcpAuthRequestedEvent;
import com.example.core_api.event.TcpConnectionEvent;
import com.example.core_api.event.TcpConnectionEvent.Status;
import com.example.core_api.token.TokenStore;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Listens for TCP reconnect events and sends the appropriate auth/reconnect request back upstream.
 */
public final class RealtimeReconnectObserver {

    private final AppEventBus eventBus;
    private final TokenStore tokenStore;
    private final TcpAuthUseCase tcpAuthUseCase;
    private final AtomicBoolean reconnectInFlight = new AtomicBoolean(false);
    private final AtomicBoolean authPending = new AtomicBoolean(true);
    private final long listenerId;

    public RealtimeReconnectObserver(@NonNull AppEventBus eventBus,
                                     @NonNull TokenStore tokenStore,
                                     @NonNull TcpAuthUseCase authUseCase) {
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
        this.tokenStore = Objects.requireNonNull(tokenStore, "tokenStore");
        this.tcpAuthUseCase = Objects.requireNonNull(authUseCase, "authUseCase");
        this.listenerId = this.eventBus.register(this::onAppEvent);
    }

    private void onAppEvent(AppEvent event) {
        if (event instanceof TcpConnectionEvent connectionEvent) {
            handleConnectionEvent(connectionEvent);
        } else if (event instanceof TcpAuthRequestedEvent) {
            handleAuthRequestEvent();
        }
    }

    private void handleConnectionEvent(TcpConnectionEvent connectionEvent) {
        if (connectionEvent.status() == Status.DISCONNECTED) {
            authPending.set(true);
            return;
        }
        if (connectionEvent.status() == Status.CONNECTED) {
            maybeAuthenticate(true);
        }
    }

    private void handleAuthRequestEvent() {
        authPending.set(true);
        maybeAuthenticate(false);
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
    }
}
