package com.sanjeevas.agrona.agent;

import org.agrona.CloseHelper;
import org.agrona.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AgentRunnerDemo {

    private static final Logger logger = LoggerFactory.getLogger(AgentRunnerDemo.class);

    public static void main(String[] args) {
        HeartbeatAgent agent = new HeartbeatAgent("heartbeat-agent");

        AgentRunner runner = new AgentRunner(
                new SleepingIdleStrategy(),
                Throwable::printStackTrace,
                null,
                agent
        );

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CloseHelper.quietClose(runner);
            logger.info("Shutdown complete.");
        }));

        AgentRunner.startOnThread(runner);
    }
}
