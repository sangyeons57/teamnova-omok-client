package com.example.core_di;

import com.example.core.event.AppEventBus;
import com.example.core.event.DefaultAppEventBus;

public final class EventBusContainer {
    private static volatile AppEventBus instance;

    private EventBusContainer() {
    }

    public static AppEventBus getInstance() {
        if (instance == null) {
            synchronized (EventBusContainer.class) {
                if (instance == null) {
                    instance = new DefaultAppEventBus();
                }
            }
        }
        return instance;
    }

    public static void setForTest(AppEventBus bus) {
        instance = bus;
    }
}
