package com.example.core.network.tcp.dispatcher;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.example.core.network.tcp.TcpClient;
import com.example.core.network.tcp.handler.ClientFrameHandler;
import com.example.core.network.tcp.handler.ClientHandlerProvider;
import com.example.core.network.tcp.protocol.Frame;
import com.example.core.network.tcp.protocol.FrameType;

/**
 * Dispatches inbound frames to handlers registered by type.
 */
public final class ClientDispatcher implements Closeable {
    private final Map<Integer, List<ClientHandlerProvider>> handlers = new ConcurrentHashMap<>();
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
        handlers.computeIfAbsent(type, key -> new CopyOnWriteArrayList<>()).add(provider);
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

    public void dispatch(TcpClient client, Frame frame) {
        Objects.requireNonNull(client, "client");
        Objects.requireNonNull(frame, "frame");
        int type = Byte.toUnsignedInt(frame.type());
        List<ClientHandlerProvider> providers = handlers.get(type);
        if (providers == null || providers.isEmpty()) {
            return;
        }
        executor.execute(() -> runHandlers(type, providers, client, frame));
    }

    @Override
    public void close() {
        if (ownsExecutor && executor instanceof ExecutorService service) {
            service.shutdownNow();
        }
        handlers.clear();
    }

    private void removeProvider(int type, ClientHandlerProvider provider) {
        handlers.compute(type, (key, list) -> {
            if (list == null) {
                return null;
            }
            list.remove(provider);
            return list.isEmpty() ? null : list;
        });
    }

    private void runHandlers(int type,
                             List<ClientHandlerProvider> providers,
                             TcpClient client,
                             Frame frame) {
        List<ClientHandlerProvider> snapshot = new ArrayList<>(providers);
        for (ClientHandlerProvider provider : snapshot) {
            ClientFrameHandler handler = provider.acquire();
            try {
                ClientDispatchResult result = handler.handle(client, frame);
                if (applyResult(type, provider, result, client, frame)) {
                    break;
                }
            } catch (Exception e) {
                System.err.println("Client handler failure for type " + type + ": " + e.getMessage());
            }
        }
    }

    private boolean applyResult(int type,
                                ClientHandlerProvider provider,
                                ClientDispatchResult result,
                                TcpClient client,
                                Frame frame) {
        if (result == null || result == ClientDispatchResult.continueDispatch()) {
            return false;
        }
        ClientDispatchContext context = new ClientDispatchContext(client, frame, this, type, provider);
        for (ClientDispatchEffect effect : result.effects()) {
            try {
                effect.apply(context);
            } catch (Exception e) {
                System.err.println("Client dispatch effect failure for type " + type + ": " + e.getMessage());
            }
        }
        if (result.removeHandler()) {
            removeProvider(type, provider);
        }
        if (result.unregisterType()) {
            handlers.remove(type);
            return true;
        }
        return result.stopPropagation();
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
