package com.nasdaq.ncdsclient.core;

/**
 * Contains information about a single Kafka broker within a cluster.
 * Provides accessors to get connection information for a specific broker, as well as
 * the ability to individually start/stop a specific Broker.
 */
public class KafkaBroker {

    private final KafkaTestServer kafkaTestServer;

    /**
     * Constructor.
     * @param kafkaTestServer Internal KafkaTestServer instance.
     */
    public KafkaBroker(final KafkaTestServer kafkaTestServer) {
        this.kafkaTestServer = kafkaTestServer;
    }

    public int getBrokerId() {
        return kafkaTestServer.getBrokerId();
    }

    /**
     * bootstrap.servers string to configure Kafka consumers or producers to access the Kafka cluster.
     * @return Connect string to use for Kafka clients.
     */
    public String getConnectString() {
        return kafkaTestServer.getKafkaConnectString();
    }

    /**
     * Starts the Kafka broker.
     * @throws Exception on startup errors.
     */
    public void start() throws Exception {
        kafkaTestServer.start();
    }

    /**
     * Stop/shutdown Kafka broker.
     * @throws Exception on shutdown errors.
     */
    public void stop() throws Exception {
        kafkaTestServer.stop();
    }

    @Override
    public String toString() {
        return "KafkaBroker{"
                + "brokerId=" + getBrokerId()
                + ", connectString='" + getConnectString() + '\''
                + '}';
    }
}