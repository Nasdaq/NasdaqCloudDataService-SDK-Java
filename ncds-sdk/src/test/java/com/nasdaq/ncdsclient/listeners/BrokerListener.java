package com.nasdaq.ncdsclient.listeners;


import java.util.Properties;

/**
 * This interface allows the caller to define and register a Listener on a Kafka Broker.
 * @see PlainListener for plaintext listeners (Default).
 * @see SslListener for SSL auth listeners.
 * @see SaslPlainListener for SASL auth listeners.
 * @see SaslSslListener for SASL+SSL auth listeners.
 */
public interface BrokerListener {

    /**
     * Returns the protocol name for the listener.
     * Examples being PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL
     * @return Protocol name.
     */
    String getProtocol();

    /**
     * Define the properties required on the broker for this listener implementation.
     * @return Properties to be registered on broker.
     */
    Properties getBrokerProperties();

    /**
     * Define the properties required on the client to connect to the broker.
     * @return Properties to be registered on connecting client.
     */
    Properties getClientProperties();
}