package com.sanjeevas.agrona;

import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class BufferProducer {

    private final UnsafeBuffer buffer;
    private static final Logger logger = LoggerFactory.getLogger(BufferProducer.class);

    public BufferProducer(UnsafeBuffer buffer) {
        this.buffer = buffer;
    }

    public void publish(String message) {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        buffer.putInt(0, bytes.length); // store message length
        buffer.putBytes(4, bytes); // write message starting from index 4
        logger.info("[Producer] Published: {}", message);
    }
}
