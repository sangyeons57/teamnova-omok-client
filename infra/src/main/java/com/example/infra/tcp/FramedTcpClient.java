package com.example.infra.tcp;

import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.example.core_api.network.tcp.TcpClient;
import com.example.core_api.network.tcp.dispatcher.ClientDispatcher;
import com.example.core_api.network.tcp.protocol.Frame;
import com.example.core_api.network.tcp.protocol.FrameDecodingException;
import com.example.core_api.network.tcp.protocol.FrameDecoder;
import com.example.core_api.network.tcp.protocol.FrameEncoder;
import com.example.core_api.network.tcp.protocol.FrameType;

/**
 * TCP client capable of speaking the Omok framed binary protocol.
 */
public final class FramedTcpClient implements TcpClient {
    private static final int READ_BUFFER_SIZE = 4096;
    private static final Duration PING_INTERVAL = Duration.ofSeconds(45);

    private final String host;
    private final int port;
    private final FrameDecoder decoder = new FrameDecoder();
    private final AtomicLong requestSequence = new AtomicLong(1);
    private final AtomicBoolean running = new AtomicBoolean();
    private final AtomicBoolean allowReconnect = new AtomicBoolean(true);
    private final Object writeLock = new Object();
    private final ClientDispatcher dispatcher;

    private SocketChannel channel;
    private Thread readerThread;
    private ScheduledFuture<?> pingTask;

    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor( r -> {
                Thread t = new Thread(r, "framed-client-scheduler");
                t.setDaemon(true);
                return t;
            });

    public FramedTcpClient(String host, int port) {
        this.host = Objects.requireNonNull(host, "host");
        this.port = port;
        this.dispatcher = ClientDispatcher.newAsyncDispatcher(1);
    }

    @Override
    public ClientDispatcher dispatcher() {
        return dispatcher;
    }

    @Override
    public synchronized void connect() throws IOException {
        allowReconnect.set(true);
        if (isConnected()) {
            return;
        }
        SocketChannel ch = SocketChannel.open();
        Log.d("FramedTcpClient", "Socket Channel Open");
        try {
            ch.configureBlocking(true);
            Log.d("FramedTcpClient", "Connecting to " + host + ":" + port);
            ch.connect(new InetSocketAddress(host, port));
            channel = ch;
            running.set(true);
            readerThread = new Thread(this::readLoop, "framed-client-reader");
            readerThread.setDaemon(true);
            readerThread.start();
            startPingLoop();
        } catch (IOException e) {
            try { ch.close(); } catch (IOException ignored) { }
            throw e;
        }
    }

    @Override
    public boolean isConnected() {
        SocketChannel ch = channel;
        return ch != null && ch.isOpen() && running.get();
    }

    @Override
    public void send(byte type, byte[] payload) throws IOException {
        SocketChannel ch = ensureConnected();
        long requestId = requestSequence.getAndIncrement() & 0xFFFF_FFFFL;
        writeFrame(ch, new Frame(type, requestId, payload));
    }


    @Override
    public void close() {
        allowReconnect.set(false);
        cancelPingLoop();
        running.set(false);
        SocketChannel ch = channel;
        if (ch != null) {
            try {
                ch.close();
            } catch (IOException ignored) {
            }
        }
        channel = null;
        if (readerThread != null) {
            readerThread.interrupt();
            try {
                readerThread.join(200);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
            readerThread = null;
        }
        dispatcher.close();
    }

    private SocketChannel ensureConnected() throws IOException {
        if (!isConnected()) {
            throw new ClosedChannelException();
        }
        return channel;
    }

    private synchronized void startPingLoop() {
        cancelPingLoop();
        pingTask = scheduler.scheduleWithFixedDelay(this::sendPingSafely,
                PING_INTERVAL.toMillis(),
                PING_INTERVAL.toMillis(),
                TimeUnit.MILLISECONDS);
    }

    private synchronized void cancelPingLoop() {
        if (pingTask != null) {
            pingTask.cancel(false);
            pingTask = null;
        }
    }

    private void sendPingSafely() {
        if (!running.get()) {
            tryReconnect();
            return;
        }
        try {
            sendPingFrame();
        } catch (IOException e) {
            Log.w("FramedTcpClient", "Ping failed, attempting reconnect", e);
            running.set(false);
            closeQuietly();
            tryReconnect();
        }
    }

    private void tryReconnect() {
        if (!allowReconnect.get() || isConnected()) {
            return;
        }
        try {
            connect();
        } catch (IOException e) {
            Log.w("FramedTcpClient", "Reconnect attempt after ping failure failed", e);
        }
    }

    private void sendPingFrame() throws IOException {
        SocketChannel ch = ensureConnected();
        Frame pingFrame = new Frame(FrameType.PING, requestSequence.getAndIncrement(), "pingpong".getBytes(StandardCharsets.UTF_8));
        writeFrame(ch, pingFrame);
    }

    private void writeFrame(SocketChannel ch, Frame frame) throws IOException {
        byte[] encoded = FrameEncoder.encode(frame);
        ByteBuffer buffer = ByteBuffer.wrap(encoded);
        synchronized (writeLock) {
            while (buffer.hasRemaining()) {
                ch.write(buffer);
            }
        }
    }

    private void readLoop() {
        SocketChannel ch = channel;
        if (ch == null) {
            return;
        }
        ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
        try {
            while (running.get()) {
                readBuffer.clear();
                int read = ch.read(readBuffer);
                if (read == -1) {
                    break;
                }
                if (read == 0) {
                    continue;
                }
                readBuffer.flip();
                byte[] chunk = new byte[readBuffer.remaining()];
                readBuffer.get(chunk);
                List<Frame> frames = decoder.feed(chunk, chunk.length);
                for (Frame frame : frames) {
                    Log.d("FramedTcpClient", "[" + frame.requestId() + "] type:" + frame.frameType() + " length:" + frame.payloadLength());
                    dispatcher.dispatch(this, frame);
                }

            }
        } catch (FrameDecodingException e) {
            Log.w("FramedTcpClient", "Frame decoding failed", e);
        } catch (IOException e) {
            Log.w("FramedTcpClient", "Read failed", e);
        } finally {
            running.set(false);
            closeQuietly();
        }
    }

    private void closeQuietly() {
        SocketChannel ch = channel;
        channel = null;
        if (ch != null) {
            try {
                ch.close();
            } catch (IOException ignored) {
            }
        }
    }
}
