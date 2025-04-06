package com.sanjeevas.aeron.ipc;

import io.aeron.Aeron;
import io.aeron.Publication;
import io.aeron.Subscription;
import org.agrona.concurrent.UnsafeBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class AeronIpcDemo {

    private static final Logger logger = LoggerFactory.getLogger(AeronIpcDemo.class);

    public static void main(String[] args) {
        // Setup Aeron context
        Aeron.Context ctx = new Aeron.Context();

        try (Aeron aeron = Aeron.connect(ctx)) {

            String channel = "aeron:ipc"; // IPC channel (intra-process)
            int streamId = 1001;

            Publication publication = aeron.addPublication(channel, streamId);
            Subscription subscription = aeron.addSubscription(channel, streamId);

            // Prepare message
            String message = "Hello via Aeron IPC";
            byte[] messageBytes = message.getBytes();
            UnsafeBuffer buffer = new UnsafeBuffer(ByteBuffer.allocateDirect(256));
            buffer.putBytes(0, messageBytes);

            // Publish message
            long result = publication.offer(buffer, 0, messageBytes.length);
            logger.info("Published? {}", (result > 0));

            // Receive message
            subscription.poll((buffer1, offset, length, header) -> {
                byte[] dst = new byte[length];
                buffer1.getBytes(offset, dst);
                logger.info("Received: {}", new String(dst));
            }, 1);

        } catch (Exception e) {
            logger.error("Error in Aeron IPC demo", e);
        }
    }
}
