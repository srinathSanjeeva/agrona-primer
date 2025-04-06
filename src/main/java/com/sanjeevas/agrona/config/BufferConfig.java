package com.sanjeevas.agrona.config;

import org.agrona.concurrent.ringbuffer.RingBufferDescriptor;
import org.agrona.concurrent.status.CountersManager;

/**
 * Utility class for computing recommended buffer sizes
 * for Agrona components like RingBuffer, CountersManager, and ErrorLog.
 */
public final class BufferConfig {

    private BufferConfig() {
        // Utility class
    }

    // ========== Ring Buffer ==========

    /**
     * Computes recommended direct buffer size for OneToOneRingBuffer.
     *
     * @param expectedMessageSize average size of a message in bytes
     * @param maxMessages         max number of messages to hold
     * @return buffer size in bytes
     */
    public static int ringBufferSize(int expectedMessageSize, int maxMessages) {
        int rawSize = expectedMessageSize * maxMessages;
        int roundedSize = nextPowerOfTwo(rawSize); // must be power of 2
        return roundedSize + RingBufferDescriptor.TRAILER_LENGTH;
    }

    // ========== Counters Manager ==========

    /**
     * Computes metadata and values buffer sizes for CountersManager.
     *
     * @param numCounters number of counters you expect to allocate
     * @return array [metadataBufferSize, valuesBufferSize]
     */
    public static int[] countersManagerSizes(int numCounters) {
        int metadataSize = CountersManager.METADATA_LENGTH * numCounters;
        int valuesSize = CountersManager.COUNTER_LENGTH * numCounters;
        return new int[]{metadataSize, valuesSize};
    }

    // ========== Distinct Error Log ==========

    /**
     * Suggests size for error buffer depending on expected exceptions.
     *
     * @param maxDistinctErrors expected distinct errors
     * @param avgStackTraceSize average stack trace size (in bytes)
     * @return buffer size in bytes
     */
    public static int errorLogBufferSize(int maxDistinctErrors, int avgStackTraceSize) {
        return nextPowerOfTwo(maxDistinctErrors * avgStackTraceSize);
    }

    // ========== Utilities ==========

    /**
     * Returns the next power-of-two greater than or equal to the given value.
     */
    public static int nextPowerOfTwo(int value) {
        if (value <= 0) return 1;
        return Integer.highestOneBit(value - 1) << 1;
    }
}
