package com.sanjeevas.agrona;

import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class BufferConsumer {

    private final UnsafeBuffer buffer;
    private static final Logger logger = LoggerFactory.getLogger(BufferConsumer.class);

    public BufferConsumer(UnsafeBuffer buffer) {
        this.buffer = buffer;
    }

    public void consume() {
        int length = buffer.getInt(0); // read length first
        byte[] bytes = new byte[length];
        buffer.getBytes(4, bytes); // read from index 4
        String message = new String(bytes, StandardCharsets.UTF_8);
        logger.info("[Consumer] Consumed: {}", message);
    }
}
