package com.example.data.repository.realtime.codec;

import com.example.application.port.out.realtime.PostGameDecisionOption;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThrows;

public final class PostGameDecisionMessageCodecTest {

    @Test
    public void encode_rematchProducesUppercaseBytes() {
        byte[] encoded = PostGameDecisionMessageCodec.encode(PostGameDecisionOption.REMATCH);

        assertArrayEquals("REMATCH".getBytes(StandardCharsets.UTF_8), encoded);
    }

    @Test
    public void encode_leaveProducesUppercaseBytes() {
        byte[] encoded = PostGameDecisionMessageCodec.encode(PostGameDecisionOption.LEAVE);

        assertArrayEquals("LEAVE".getBytes(StandardCharsets.UTF_8), encoded);
    }

    @Test
    public void encode_unknownThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> PostGameDecisionMessageCodec.encode(PostGameDecisionOption.UNKNOWN));
    }
}
