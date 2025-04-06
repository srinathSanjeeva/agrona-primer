package com.sanjeevas.agrona.agent;

import org.agrona.collections.Object2IntHashMap;
import org.agrona.collections.Object2ObjectHashMap;
import org.agrona.collections.ObjectHashSet;
import org.agrona.concurrent.Agent;
import org.agrona.concurrent.EpochClock;
import org.agrona.concurrent.status.AtomicCounter;
import org.agrona.concurrent.errors.DistinctErrorLog;
import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ErrorMonitoringAgent implements Agent {

    private static final Logger logger = LoggerFactory.getLogger(ErrorMonitoringAgent.class);

    private final DistinctErrorLog errorLog;
    private final AtomicCounter tickCounter;
    private final EpochClock clock;
    // A map to track error frequency
    private final Object2IntHashMap<String> errorFrequencyMap = new Object2IntHashMap<>(-1);
    private final Object2IntHashMap<String> lastLoggedCountMap = new Object2IntHashMap<>(-1);
    private final ObjectHashSet<String> printedStackTraces = new ObjectHashSet<>();
    // A Object2HasMap to store error details
    private final Object2ObjectHashMap<String, Throwable> errorDetailsMap = new Object2ObjectHashMap<>();




    private long lastLogTimeMillis = 0;

    public ErrorMonitoringAgent(DistinctErrorLog errorLog, EpochClock clock, AtomicCounter tickCounter) {
        this.errorLog = errorLog;
        this.clock = clock;
        this.tickCounter = tickCounter;
    }

    @Override
    public int doWork() {
        long now = clock.time();

        if (now - lastLogTimeMillis > 5000) { // Log every 5 seconds
            logger.info("[ErrorAgent] Total errors observed: {}", tickCounter.get());
            lastLogTimeMillis = now;

            errorFrequencyMap.forEach((key, count) -> {
                int lastLogged = lastLoggedCountMap.getOrDefault(key, -1);
                if (count > lastLogged) {
                    logger.warn("Error [{}] reoccurred. Count now: {}", key, count);
                    lastLoggedCountMap.put(key, count); // update tracker
                }
            });
        }


        return 1;
    }

    @Override
    public String roleName() {
        return "error-monitoring-agent";
    }

    public void record(Throwable t) {
        errorLog.record(t);
        tickCounter.increment();

        String key = t.getClass().getSimpleName() + ": " + t.getMessage();
        incrementErrorCount(key);

        // Save reference to Throwable for later debugging/analysis
        errorDetailsMap.putIfAbsent(key, t);  // Only keep first instance

        if (!printedStackTraces.contains(key)) {
            logger.error("New error occurred [{}], printing stack trace once:", key, t);
            printedStackTraces.add(key);
        }
    }


    private void incrementErrorCount(String key) {
//        int current = errorFrequencyMap.getOrDefault(key, -1); Alternative way to get value
        int current = errorFrequencyMap.getValue(key);
        if (current == errorFrequencyMap.missingValue()) {
            // First occurrence
            errorFrequencyMap.put(key, 1);
        } else {
            errorFrequencyMap.put(key, current + 1);
        }
    }


}
