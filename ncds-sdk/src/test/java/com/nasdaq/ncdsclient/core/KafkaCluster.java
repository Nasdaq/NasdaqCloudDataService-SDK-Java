package com.nasdaq.ncdsclient.core;

/**
 * This interface abstracts away knowing if the underlying 'kafka cluster' is a single server (KafkaTestServer)
 * or a cluster of 1 or more brokers (KafkaTestCluster).
 */
public interface KafkaCluster extends KafkaProvider, AutoCloseable {

    /**
     * Creates and starts ZooKeeper and Kafka server instances.
     * @throws Exception on startup errors.
     */
    void start() throws Exception;

    /**
     * Closes the internal servers. Failing to call this at the end of your tests will likely
     * result in leaking instances.
     */
    void close() throws Exception;
}