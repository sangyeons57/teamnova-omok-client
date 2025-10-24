package com.example.core_api.event;

public interface AppEventBus {
    long register(AppEventListener listener);
    void unregister(long id);
    void post(AppEvent event);
    void postAsync(AppEvent event);
}
