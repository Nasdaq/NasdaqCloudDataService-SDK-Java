package com.nasdaq.ncdsclient.core;

import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.TestingServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ZookeeperTestServer implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperTestServer.class);

    /**
     * Internal Zookeeper test server instance.
     */
    private TestingServer zkServer = null;

    /**
     * Starts the internal Test zookeeper server instance.
     */
    public void start() {
        try {
            if (zkServer == null) {
                // Define configuration
                final InstanceSpec zkInstanceSpec = new InstanceSpec(
                        Utils.createTempDirectory(),
                        -1,
                        -1,
                        -1,
                        false,
                        -1,
                        -1,
                        1000
                );

                // Create instance
                logger.info("Starting Zookeeper test server");
                zkServer = new TestingServer(zkInstanceSpec, true);
            } else {
                // Instance already exists, so 'start' by calling restart on instance.
                logger.info("Restarting Zookeeper test server");
                zkServer.restart();
            }
        } catch (final Exception exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    /**
     * Restarts the internal Test zookeeper server instance.
     */
    public void restart() {
        // If we have no instance yet
        if (zkServer == null) {
            // Call start instead and return.
            start();
            return;
        }

        // Otherwise call restart.
        try {
            zkServer.restart();
        } catch (final Exception exception) {
            throw new RuntimeException(exception.getMessage(), exception);
        }
    }

    /**
     * Stops the internal Test zookeeper server instance.
     */
    public void stop() {
        logger.info("Shutting down zookeeper test server");

        // If we don't have an instance
        if (zkServer != null) {
            try {
                // Call stop, this keeps the temporary data on disk around
                // and allows us to be able to restart it again later.
                zkServer.stop();
            } catch (final IOException exception) {
                throw new RuntimeException(exception.getMessage(), exception);
            }
        }
    }

    /**
     * Alias for stop().
     */
    @Override
    public void close() {
        stop();
    }

    /**
     * Returns connection string for zookeeper clients.
     * @return Connection string to connect to the Zookeeper instance.
     * @throws IllegalStateException if start() has not been called yet.
     */
    public String getConnectString() {
        if (zkServer == null) {
            throw new IllegalStateException("Cannot get connect string before service is started.");
        }
        return zkServer.getConnectString();
    }
}
