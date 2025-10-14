package com.example.data.repository.realtime.codec;

import com.example.application.port.out.realtime.PostGameDecisionAck;
import com.example.application.port.out.realtime.PostGameDecisionOption;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class PostGameDecisionMessageCodecTest {

    @Test
    public void decodeAck_returnsOkForValidSuccessPayload() {
        String payload = "{\"status\":\"OK\",\"decision\":\"REMATCH\"}";

        PostGameDecisionAck ack = PostGameDecisionMessageCodec.decodeAck(payload.getBytes());

        assertEquals(PostGameDecisionAck.Status.OK, ack.status());
        assertEquals(PostGameDecisionOption.REMATCH, ack.decision());
        assertEquals(PostGameDecisionAck.ErrorReason.NONE, ack.errorReason());
    }

    @Test
    public void decodeAck_returnsErrorForReasonPayload() {
        String payload = "{\"status\":\"ERROR\",\"reason\":\"ALREADY_DECIDED\"}";

        PostGameDecisionAck ack = PostGameDecisionMessageCodec.decodeAck(payload.getBytes());

        assertEquals(PostGameDecisionAck.Status.ERROR, ack.status());
        assertEquals(PostGameDecisionAck.ErrorReason.ALREADY_DECIDED, ack.errorReason());
    }
}
