package com.sanjeevas.agrona;

import org.agrona.collections.*;
import org.agrona.concurrent.*;
import org.agrona.concurrent.errors.DistinctErrorLog;
import org.agrona.concurrent.errors.ErrorConsumer;
import org.agrona.concurrent.ringbuffer.OneToOneRingBuffer;
import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.agrona.concurrent.status.AtomicCounter;
import org.agrona.concurrent.status.CountersManager;
import org.agrona.io.DirectBufferInputStream;
import org.agrona.io.DirectBufferOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class AgronaAllFeaturesDemo {

    private static final Logger logger = LoggerFactory.getLogger(AgronaAllFeaturesDemo.class);

    public static void main(String[] args) throws IOException {

        // ========== Buffer Initialization ==========
        // ========== Buffers ==========
        AtomicBuffer onHeapBuffer = new UnsafeBuffer(new byte[64]);
        AtomicBuffer offHeapBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(64));
        onHeapBuffer.putInt(0, 123);
        logger.info("AtomicBuffer (on-heap) read: {}", onHeapBuffer.getInt(0));

        // ========== Off-Heap Buffer Usage ==========
        String offHeapMessage = "OffHeapRocks!";
        byte[] offHeapBytes = offHeapMessage.getBytes(StandardCharsets.UTF_8);

        // Write to off-heap
        offHeapBuffer.putBytes(0, offHeapBytes);
        logger.info("Wrote to offHeapBuffer: {}", offHeapMessage);

        // Read from off-heap
        byte[] readBack = new byte[offHeapBytes.length];
        offHeapBuffer.getBytes(0, readBack);
        String readBackStr = new String(readBack, StandardCharsets.UTF_8);
        logger.info("Read from offHeapBuffer: {}", readBackStr);


        // ========== Lists ==========
        IntArrayList intList = new IntArrayList();
        intList.addInt(10);
        intList.addInt(20);
        logger.info("IntArrayList: {}", intList);

        LongArrayList longList = new LongArrayList();
        longList.addLong(100L);
        logger.info("LongArrayList: {}", longList);

        // ========== Maps ==========
        Int2ObjectHashMap<String> intToObjMap = new Int2ObjectHashMap<>();
        intToObjMap.put(1, "One");
        logger.info("Int2ObjectHashMap: {}", intToObjMap.get(1));

        Int2IntHashMap intToIntMap = new Int2IntHashMap(-1);
        intToIntMap.put(1, 100);
        logger.info("Int2IntHashMap: {}", intToIntMap.get(1));

        Long2LongHashMap longToLongMap = new Long2LongHashMap(-1);
        longToLongMap.put(101L, 1000L);
        logger.info("Long2LongHashMap: {}", longToLongMap.get(101L));

        // ========== Sets ==========
        IntHashSet intSet = new IntHashSet();
        intSet.add(42);
        logger.info("IntHashSet contains 42: {}", intSet.contains(42));

        ObjectHashSet<String> objectSet = new ObjectHashSet<>();
        objectSet.add("fast");
        logger.info("ObjectHashSet contains 'fast': {}", objectSet.contains("fast"));

        // ========== Clocks ==========
        EpochClock epochClock = SystemEpochClock.INSTANCE;
        NanoClock nanoClock = SystemNanoClock.INSTANCE;
        logger.info("Epoch time: {}", epochClock.time());
        logger.info("Nano time: {}", nanoClock.nanoTime());

        // ========== Queues ==========
        ManyToOneConcurrentLinkedQueue<String> queue = new ManyToOneConcurrentLinkedQueue<>();
        queue.offer("message");
        logger.info("Polled from queue: {}", queue.poll());

        // ========== Ring Buffer ==========
        UnsafeBuffer ringBufferBacking = new UnsafeBuffer(ByteBuffer.allocateDirect(1024 + RingBufferDescriptor.TRAILER_LENGTH));
        OneToOneRingBuffer ringBuffer = new OneToOneRingBuffer(ringBufferBacking);

        String msg = "HelloAgrona";
        byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
        UnsafeBuffer srcBuffer = new UnsafeBuffer(msgBytes);
        boolean success = ringBuffer.write(1, srcBuffer, 0, msgBytes.length);
        logger.info("Ring buffer write success: {}", success);

        ringBuffer.read((msgTypeId, buffer, index, length) -> {
            byte[] receivedBytes = new byte[length];
            buffer.getBytes(index, receivedBytes);
            logger.info("Ring buffer read: {}", new String(receivedBytes, StandardCharsets.UTF_8));
        });

        // ========== Counters ==========
        int counterCapacity = 16;
        int metadataBufferSize = CountersManager.METADATA_LENGTH * counterCapacity; // 1024 * 16 = 16 KB
        int valuesBufferSize = CountersManager.COUNTER_LENGTH * counterCapacity;     // 128 * 16 = 2 KB

        UnsafeBuffer labelsBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(metadataBufferSize));
        UnsafeBuffer valuesBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(valuesBufferSize));

        CountersManager countersManager = new CountersManager(labelsBuffer, valuesBuffer);
        // Wrap the created AtomicCounters in try‑with‑resources.
        try (AtomicCounter ordersCounter = countersManager.newCounter("orders-tracked");
             AtomicCounter tradesCounter = countersManager.newCounter("trades-executed")) {

            ordersCounter.increment();
            logger.info("Orders counter: {}", ordersCounter.get());

            tradesCounter.increment();
            logger.info("Trades counter: {}", tradesCounter.get());
        }


        // ========== Stream wrappers ==========
        UnsafeBuffer streamBuf = new UnsafeBuffer(ByteBuffer.allocateDirect(64));
        try (DirectBufferOutputStream outputStream = new DirectBufferOutputStream(streamBuf)) {
            outputStream.write("streamData".getBytes(StandardCharsets.UTF_8));
        }

        try (DirectBufferInputStream inputStream = new DirectBufferInputStream(streamBuf)) {
            byte[] streamBytes = new byte["streamData".length()];
            int numBytesRead = inputStream.read(streamBytes);
            if (numBytesRead == -1) {
                logger.error("End of stream reached");
            } else {
                logger.info("Number of bytes read: {}", numBytesRead);
            }
            logger.info("Read from DirectBufferInputStream: {}", new String(streamBytes, StandardCharsets.UTF_8));
        }

        // ========== Error Log ==========
        UnsafeBuffer errorBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(4096));
        ErrorConsumer errorConsumer = (count, first, last, error) -> logger.error("Observed non-Throwable error: {}", error);


        // TODO: This version of Agrona doesn't seem to have a DistinctErrorLog constructor that takes an ErrorConsumer

        DistinctErrorLog errorLog = new DistinctErrorLog(errorBuffer, epochClock);
        errorLog.record(new RuntimeException("Simulated error"));


        errorLog.record(new RuntimeException("Simulated error"));


        logger.info("Logged a simulated error");


        // ========== Signal Handling ==========
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        SigInt.register(barrier::signal);
        logger.info("Waiting for Ctrl+C (SIGINT) to exit...");
        barrier.await();
        logger.info("Shutdown signal received. Exiting.");
    }
}
