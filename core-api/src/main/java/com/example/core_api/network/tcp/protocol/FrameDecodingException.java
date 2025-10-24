package com.example.core_api.network.tcp.protocol;

/**
 * Thrown when the inbound byte stream violates the framing rules.
 */
public final class FrameDecodingException extends Exception {
    public FrameDecodingException(String message) {
        super(message);
    }
}
