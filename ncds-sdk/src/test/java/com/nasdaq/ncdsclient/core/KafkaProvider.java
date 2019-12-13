package com.nasdaq.ncdsclient.core;

import java.util.List;

/**
 * Provides a slimmed down view onto KafkaCluster to avoid circular references in code.
 */
public interface KafkaProvider {
    /**
     * Returns an immutable list of broker hosts for the kafka cluster.
     * @return immutable list of hosts for brokers within the cluster.
     */
    KafkaBrokers getKafkaBrokers();

    /**
     * bootstrap.servers string to configure Kafka consumers or producers to access the Kafka cluster.
     * @return Connect string to use for Kafka clients.
     */
    String getKafkaConnectString();

    /**
     * Connection details about each of the registered listeners on the kafka broker.
     * @return details about each of the registered listeners on the kafka broker.
     */
    List<ListenerProperties> getListenerProperties();

    /**
     * Returns connection string for zookeeper clients.
     * @return Connection string to connect to the Zookeeper instance.
     */
    String getZookeeperConnectString();
}