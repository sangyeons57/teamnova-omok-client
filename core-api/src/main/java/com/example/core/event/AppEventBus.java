package com.example.core.event;

import java.util.concurrent.Future;

public interface AppEventBus {
    long register(AppEventListener listener);
    void unregister(long id);
    void post(AppEvent event);
    void postAsync(AppEvent event);
}
