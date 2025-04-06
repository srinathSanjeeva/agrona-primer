package com.sanjeevas.agrona.threadsafe;

import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public class ThreadSafeReaderAgent implements Runnable {

    private final UnsafeBuffer buffer;
    private final Object lock;
    private static final Logger logger = LoggerFactory.getLogger(ThreadSafeReaderAgent.class);

    public ThreadSafeReaderAgent(UnsafeBuffer buffer, Object lock) {
        this.buffer = buffer;
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(100); // Let writer run first
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        synchronized (lock) {
            int length = buffer.getInt(0);
            byte[] dst = new byte[length];
            buffer.getBytes(4, dst);
            String result = new String(dst, StandardCharsets.UTF_8);
            logger.info("[ReaderAgent] Read: {}", result);
        }
    }
}
