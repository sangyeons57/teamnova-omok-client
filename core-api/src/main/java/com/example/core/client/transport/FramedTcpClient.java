package com.example.core.client.transport;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import com.example.core.client.dispatcher.ClientDispatcher;
import teamnova.omok.client.handler.ClientHandlerRegistry;
import teamnova.omok.client.protocol.Frame;
import teamnova.omok.client.protocol.FrameDecodingException;
import teamnova.omok.client.protocol.FrameDecoder;
import teamnova.omok.client.protocol.FrameEncoder;
import teamnova.omok.client.protocol.FrameType;
import teamnova.omok.dispatcher.Dispatcher;

/**
 * TCP client capable of speaking the Omok framed binary protocol.
 */
public final class FramedTcpClient implements Closeable {
    private static final int READ_BUFFER_SIZE = 4096;

    private final String host;
    private final int port;
    private final FrameDecoder decoder = new FrameDecoder();
    private final AtomicLong requestSequence = new AtomicLong(1);
    private final Map<Long, CompletableFuture<Frame>> pending = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean();
    private final Object writeLock = new Object();
    private final ClientDispatcher dispatcher;

    private SocketChannel channel;
    private Thread readerThread;

    public FramedTcpClient(String host, int port, ClientHandlerRegistry registry) {
        this.host = Objects.requireNonNull(host, "host");
        this.port = port;
        this.dispatcher = ClientDispatcher.newAsyncDispatcher(1);
        Objects.requireNonNull(registry, "registry").configure(this.dispatcher);
    }

    public synchronized void connect() throws IOException {
        if (isConnected()) {
            return;
        }
        SocketChannel ch = SocketChannel.open();
        try {
            ch.configureBlocking(true);
            ch.connect(new InetSocketAddress(host, port));
            channel = ch;
            running.set(true);
            readerThread = new Thread(this::readLoop, "framed-client-reader");
            readerThread.setDaemon(true);
            readerThread.start();
        } catch (IOException e) {
            try { ch.close(); } catch (IOException ignored) { }
            throw e;
        }
    }

    public boolean isConnected() {
        SocketChannel ch = channel;
        return ch != null && ch.isOpen() && running.get();
    }

    public CompletableFuture<Frame> send(byte type, byte[] payload) throws IOException {
        SocketChannel ch = ensureConnected();
        long requestId = requestSequence.getAndIncrement() & 0xFFFF_FFFFL;
        Frame frame = new Frame(type, requestId, payload != null ? payload : new byte[0]);
        CompletableFuture<Frame> future = new CompletableFuture<>();
        pending.put(requestId, future);
        try {
            writeFrame(ch, frame);
        } catch (IOException e) {
            CompletableFuture<Frame> removed = pending.remove(requestId);
            if (removed != null) {
                removed.completeExceptionally(e);
            }
            throw e;
        }
        return future;
    }

    public CompletableFuture<Frame> send(FrameType type, byte[] payload) throws IOException {
        Objects.requireNonNull(type, "type");
        return send(type.code(), payload);
    }

    public void sendAndForget(byte type, byte[] payload) throws IOException {
        SocketChannel ch = ensureConnected();
        Frame frame = new Frame(type, 0, payload != null ? payload : new byte[0]);
        writeFrame(ch, frame);
    }

    public void sendAndForget(FrameType type, byte[] payload) throws IOException {
        Objects.requireNonNull(type, "type");
        sendAndForget(type.code(), payload);
    }

    @Override
    public void close() {
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
        IOException closed = new ClosedChannelException();
        pending.forEach((id, future) -> future.completeExceptionally(closed));
        pending.clear();
        dispatcher.close();
    }

    private SocketChannel ensureConnected() throws IOException {
        if (!isConnected()) {
            throw new ClosedChannelException();
        }
        return channel;
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
                    handleInboundFrame(frame);
                }
            }
        } catch (FrameDecodingException e) {
            failPending(new IOException("Failed to decode inbound frame: " + e.getMessage(), e));
        } catch (IOException e) {
            failPending(e);
        } finally {
            running.set(false);
            closeQuietly();
        }
    }

    private void handleInboundFrame(Frame frame) {
        CompletableFuture<Frame> future = pending.remove(frame.requestId());
        if (future != null) {
            future.complete(frame);
        }
        dispatcher.dispatch(this, frame);
    }

    private void failPending(IOException error) {
        pending.forEach((id, future) -> future.completeExceptionally(error));
        pending.clear();
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
