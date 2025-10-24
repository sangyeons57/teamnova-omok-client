package com.example.core_api.event;

@FunctionalInterface
public interface AppEventListener {
    void onEvent(AppEvent event);
}
