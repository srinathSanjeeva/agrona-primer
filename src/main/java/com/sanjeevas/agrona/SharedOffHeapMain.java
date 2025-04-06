package com.sanjeevas.agrona;

import org.agrona.concurrent.UnsafeBuffer;

import java.nio.ByteBuffer;

public class SharedOffHeapMain {

    public static void main(String[] args) {

        // Allocate a shared off-heap buffer (e.g. 1KB)
        UnsafeBuffer sharedBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(1024));

        // Create producer and consumer using the same buffer
        BufferProducer producer = new BufferProducer(sharedBuffer);
        BufferConsumer consumer = new BufferConsumer(sharedBuffer);

        // Simulate write and read
        String payload = "Message shared across classes using off-heap buffer!";
        producer.publish(payload);
        consumer.consume();
    }
}
