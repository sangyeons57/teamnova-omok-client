package com.example.core.event;

@FunctionalInterface
public interface AppEventListener {
    void onEvent(AppEvent event);
}
