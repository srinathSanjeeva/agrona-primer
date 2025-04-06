package com.sanjeevas.agrona.agent;

import com.sanjeevas.agrona.config.BufferConfig;
import org.agrona.concurrent.*;
import org.agrona.concurrent.errors.DistinctErrorLog;
import org.agrona.concurrent.status.AtomicCounter;
import org.agrona.concurrent.status.CountersManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class ErrorAgentRunnerMain {

    private static final Logger logger = LoggerFactory.getLogger(ErrorAgentRunnerMain.class);

    public static void main(String[] args) {

        EpochClock clock = SystemEpochClock.INSTANCE;
        int errorBufferSize = BufferConfig.errorLogBufferSize(100, 8192);
        UnsafeBuffer errorBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(errorBufferSize));
        DistinctErrorLog errorLog = new DistinctErrorLog(errorBuffer, clock);

        int[] counterSizes = BufferConfig.countersManagerSizes(16); // Enough for 16 counters

        UnsafeBuffer labelsBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(counterSizes[0]));
        UnsafeBuffer valuesBuffer = new UnsafeBuffer(ByteBuffer.allocateDirect(counterSizes[1]));
        CountersManager countersManager = new CountersManager(labelsBuffer, valuesBuffer);
        AtomicCounter errorCounter = countersManager.newCounter("agent-errors-count");

        ErrorMonitoringAgent agent = new ErrorMonitoringAgent(errorLog, clock, errorCounter);

        AgentRunner runner = new AgentRunner(
                new SleepingIdleStrategy(),
                Throwable::printStackTrace,
                null,
                agent
        );

        Thread runnerThread = new Thread(runner);
        runnerThread.setName("error-monitoring-agent");
        runnerThread.start();

        // Simulate periodic errors
        for (int i = 0; i < 10; i++) {
            agent.record(new RuntimeException("Simulated issue #" + i));
            try {
                Thread.sleep(700); // simulate runtime
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Graceful shutdown
        ShutdownSignalBarrier barrier = new ShutdownSignalBarrier();
        SigInt.register(barrier::signal);

        logger.info("Waiting for Ctrl+C to terminate...");
        barrier.await(); // waits for SIGINT (Ctrl+C)

        // Then close runner gracefully
        runner.close();
        logger.info("Agent shut down.");

    }
}
