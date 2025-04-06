package com.sanjeevas.agrona.threadsafe;

import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class ThreadSafeOffHeapMain {

    public static void main(String[] args) {

        // Allocate shared off-heap buffer
        UnsafeBuffer sharedBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));
        Object lock = new Object(); // Shared lock object

        // Create writer and reader agents
        String message = "Hello from shared off-heap memory with threads!";
        Thread writer = new Thread(new ThreadSafeWriterAgent(sharedBuffer, lock, message));
        Thread reader = new Thread(new ThreadSafeReaderAgent(sharedBuffer, lock));

        // Start threads
        writer.start();
        reader.start();

        // Wait for completion
        try {
            writer.join();
            reader.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
