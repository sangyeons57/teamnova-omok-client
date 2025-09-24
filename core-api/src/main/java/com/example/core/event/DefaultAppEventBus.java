package com.example.core.event;

import android.util.Log;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class DefaultAppEventBus implements AppEventBus {
    private final static String TAG = "DefaultAppEventBus";
    private final AtomicLong idGenerator = new AtomicLong();
    private final ConcurrentHashMap<Long, AppEventListener> listeners = new ConcurrentHashMap<>();

    // 합리적인 디폴트: 코어 N, 최대 2N, 제한 큐
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            1,1,
            0L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(10_000),
            r -> {
                Thread t = new Thread(r, "eventbus-" + System.nanoTime());
                t.setDaemon(true);
                return t;
            },
            // 백프레셔: 가득 차면 호출 스레드에서 실행
            (r, ex) -> { if (!ex.isShutdown()) r.run(); }
    );

    @Override
    public long register(AppEventListener listener) {
        Objects.requireNonNull(listener, "listener");
        Log.d(TAG, "register: " + listener.getClass().getSimpleName());
        long id = idGenerator.incrementAndGet();
        listeners.put(id, listener);
        return id;
    }

    @Override
    public void unregister(long id) {
        Log.d(TAG, "unregister: " + id);
        listeners.remove(id);
    }

    @Override
    public void post(AppEvent event) {
        Objects.requireNonNull(event, "event");
        Log.d(TAG, "post: " + event.getClass().getSimpleName());
        for (AppEventListener listener : listeners.values()) {
            try {
                listener.onEvent(event);
            } catch (RuntimeException ignored) {
                Log.e(TAG, "post: " + event.getClass().getSimpleName(), ignored);
                // Protect the bus from listener failures.
            }
        }
    }

    @Override
    public void postAsync(AppEvent event) {
        Log.d(TAG, "postAsync: " + event.getClass().getSimpleName());
        executor.execute(() -> post(event));
    }
}
