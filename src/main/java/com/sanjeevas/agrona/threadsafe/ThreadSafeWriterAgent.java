package com.sanjeevas.agrona.threadsafe;

import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ThreadSafeWriterAgent implements Runnable {

    private final UnsafeBuffer buffer;
    private final Object lock;
    private final String message;
    private static final Logger logger = LoggerFactory.getLogger(ThreadSafeWriterAgent.class);

    public ThreadSafeWriterAgent(UnsafeBuffer buffer, Object lock, String message) {
        this.buffer = buffer;
        this.lock = lock;
        this.message = message;
    }

    @Override
    public void run() {
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        synchronized (lock) {
            buffer.putInt(0, bytes.length);
            buffer.putBytes(4, bytes);
            logger.info("[WriterAgent] Wrote: {}", message);
        }
    }
}
