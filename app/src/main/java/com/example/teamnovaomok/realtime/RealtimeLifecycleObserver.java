package com.example.teamnovaomok.realtime;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.example.core_api.event.AppEventBus;
import com.example.core_api.event.TcpConnectRequestedEvent;

import java.util.Objects;

public final class RealtimeLifecycleObserver implements DefaultLifecycleObserver {

    private final AppEventBus eventBus;

    public RealtimeLifecycleObserver(@NonNull AppEventBus eventBus) {
        this.eventBus = Objects.requireNonNull(eventBus, "eventBus");
    }

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        eventBus.postAsync(TcpConnectRequestedEvent.INSTANCE);
    }
}
