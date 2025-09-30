package com.example.core.client.dispatcher;

import java.io.Closeable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import teamnova.omok.client.handler.ClientFrameHandler;
import teamnova.omok.client.handler.ClientHandlerProvider;
import teamnova.omok.client.protocol.Frame;
import teamnova.omok.client.protocol.FrameType;
import teamnova.omok.client.transport.FramedTcpClient;

/**
 * Dispatches inbound frames to handlers registered by type.
 */
public final class ClientDispatcher implements Closeable {
    private final Map<Integer, ClientHandlerProvider> handlers = new ConcurrentHashMap<>();
    private final Executor executor;
    private final boolean ownsExecutor;

    public ClientDispatcher(Executor executor) {
        this(executor, false);
    }

    private ClientDispatcher(Executor executor, boolean ownsExecutor) {
        this.executor = Objects.requireNonNull(executor, "executor");
        this.ownsExecutor = ownsExecutor;
    }

    public static ClientDispatcher newAsyncDispatcher(int threads) {
        int size = Math.max(1, threads);
        ExecutorService executor = Executors.newFixedThreadPool(size, new ClientDispatcherThreadFactory());
        return new ClientDispatcher(executor, true);
    }

    public static ClientDispatcher directDispatcher() {
        return new ClientDispatcher(Runnable::run, false);
    }

    public void register(FrameType type, ClientHandlerProvider provider) {
        Objects.requireNonNull(type, "type");
        register(Byte.toUnsignedInt(type.code()), provider);
    }

    public void register(byte type, ClientHandlerProvider provider) {
        register(Byte.toUnsignedInt(type), provider);
    }

    public void register(int type, ClientHandlerProvider provider) {
        if (type < 0 || type > 255) {
            throw new IllegalArgumentException("type must be within 0-255 range");
        }
        Objects.requireNonNull(provider, "provider");
        ClientHandlerProvider previous = handlers.putIfAbsent(type, provider);
        if (previous != null) {
            throw new IllegalStateException("Handler already registered for type " + type);
        }
    }

    public void unregister(FrameType type) {
        if (type != null) {
            unregister(Byte.toUnsignedInt(type.code()));
        }
    }

    public void unregister(byte type) {
        unregister(Byte.toUnsignedInt(type));
    }

    public void unregister(int type) {
        if (type < 0 || type > 255) {
            return;
        }
        handlers.remove(type);
    }

    public void dispatch(FramedTcpClient client, Frame frame) {
        Objects.requireNonNull(client, "client");
        Objects.requireNonNull(frame, "frame");
        int type = Byte.toUnsignedInt(frame.type());
        ClientHandlerProvider provider = handlers.get(type);
        if (provider == null) {
            return;
        }
        executor.execute(() -> {
            ClientFrameHandler handler = provider.acquire();
            try {
                handler.handle(client, frame);
            } catch (Exception e) {
                System.err.println("Client handler failure for type " + type + ": " + e.getMessage());
            }
        });
    }

    @Override
    public void close() {
        if (ownsExecutor && executor instanceof ExecutorService service) {
            service.shutdownNow();
        }
        handlers.clear();
    }

    private static final class ClientDispatcherThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("client-dispatcher-" + counter.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        }
    }
}
