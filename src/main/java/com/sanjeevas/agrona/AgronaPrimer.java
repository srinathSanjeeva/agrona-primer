package com.sanjeevas.agrona;

// Using Agrona's high-performance data structures: Int2ObjectHashMap and ExpandableArrayBuffer

import org.agrona.ExpandableArrayBuffer;
import org.agrona.collections.Int2ObjectHashMap;
import org.agrona.collections.IntHashSet;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class AgronaPrimer {

    private static final Logger logger = LoggerFactory.getLogger(AgronaPrimer.class);

    public static void main(String[] args) {

        // Using Int2ObjectHashMap for efficient integer to object mapping
        Int2ObjectHashMap<String> map = new Int2ObjectHashMap<>();
        map.put(1, "OpenHFT");
        map.put(2, "Agrona");
        map.put(3, "Low Latency");
        logger.info("Retrieved value for key 2: {}", map.get(2));
        map.forEach((key, value) -> logger.info("Key: {}, Value: {}", key, value));

        // Using ExpandableArrayBuffer for efficient byte buffer operations
        ExpandableArrayBuffer buffer = new ExpandableArrayBuffer();
        String data = "High-performance trading";
        byte[] bytes = data.getBytes();
        buffer.putBytes(0, bytes);
        UnsafeBuffer unsafeBuffer = new UnsafeBuffer(buffer);
        byte[] readBytes = new byte[bytes.length];
        unsafeBuffer.getBytes(0, readBytes);
        String readData = new String(readBytes);
        logger.info("Data read from buffer: {}", readData);

        // Using IntHashSet for fast set operations
        IntHashSet intSet = new IntHashSet();
        intSet.add(101);
        intSet.add(202);
        intSet.add(303);
        intSet.forEach(value -> logger.info("IntHashSet value: {}", value));
        logger.info("Does set contain 202? {}", intSet.contains(202));

        // Using OneToOneRingBuffer for low-latency inter-thread messaging
        UnsafeBuffer ringBufferBacking = new UnsafeBuffer(ByteBuffer.allocateDirect(1024 + RingBufferDescriptor.TRAILER_LENGTH));
        OneToOneRingBuffer ringBuffer = new OneToOneRingBuffer(ringBufferBacking);

        String msg = "Fast message";
        byte[] msgBytes = msg.getBytes();
        boolean written = ringBuffer.write(1, new UnsafeBuffer(msgBytes), 0, msgBytes.length);
        logger.info("Message written to ring buffer: {}", written);

        ringBuffer.read((msgTypeId, buffer1, index, length) -> {
            byte[] dst = new byte[length];
            buffer1.getBytes(index, dst);
            logger.info("Received message from ring buffer: {}", new String(dst));
        });
    }
}