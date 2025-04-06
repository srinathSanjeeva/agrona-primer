package com.sanjeevas.agrona.agent;

import org.agrona.concurrent.Agent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartbeatAgent implements Agent {

    private static final Logger logger = LoggerFactory.getLogger(HeartbeatAgent.class);
    private final String roleName;

    public HeartbeatAgent(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public int doWork() {
        logger.info("[{}] Heartbeat tick", roleName);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return 1;
    }

    @Override
    public String roleName() {
        return roleName;
    }

    @Override
    public void onClose() {
        logger.info("[{}] Agent shutting down", roleName);
    }
}
