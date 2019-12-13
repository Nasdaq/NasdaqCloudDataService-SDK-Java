package com.nasdaq.ncdsclient.listeners;

import java.util.Properties;

/**
 * Define and register an SSL listener on a Kafka broker.
 */
public class SslListener implements BrokerListener {

    private String trustStoreFile = "";
    private String trustStorePassword = "";
    private String keyStoreFile = "";
    private String keyStorePassword = "";
    private String keyPassword = "";
    private String clientAuth = "required";

    /**
     * Setter.
     * @param trustStoreLocation file path to TrustStore JKS file.
     * @return SslListener for method chaining.
     */
    public SslListener withTrustStoreLocation(final String trustStoreLocation) {
        this.trustStoreFile = trustStoreLocation;
        return this;
    }

    /**
     * Setter.
     * @param keyStoreLocation file path to KeyStore JKS file.
     * @return SslListener for method chaining.
     */
    public SslListener withKeyStoreLocation(final String keyStoreLocation) {
        this.keyStoreFile = keyStoreLocation;
        return this;
    }

    /**
     * Setter.
     * @param password Password for TrustStore.
     * @return SslListener for method chaining.
     */
    public SslListener withTrustStorePassword(final String password) {
        this.trustStorePassword = password;
        return this;
    }

    /**
     * Setter.
     * @param password Password for KeyStore.
     * @return SslListener for method chaining.
     */
    public SslListener withKeyStorePassword(final String password) {
        this.keyStorePassword = password;
        return this;
    }

    /**
     * Setter.
     * @param password Password for Key.
     * @return SslListener for method chaining.
     */
    public SslListener withKeyPassword(final String password) {
        this.keyPassword = password;
        return this;
    }

    /**
     * Set client auth as required.
     * @return SslListener for method chaining.
     */
    public SslListener withClientAuthRequired() {
        this.clientAuth = "required";
        return this;
    }

    /**
     * Set client auth as requested, but not required.
     * @return SslListener for method chaining.
     */
    public SslListener withClientAuthRequested() {
        this.clientAuth = "requested";
        return this;
    }

    @Override
    public String getProtocol() {
        return "SSL";
    }

    @Override
    public Properties getBrokerProperties() {
        final Properties properties = new Properties();
        properties.put("ssl.truststore.location", trustStoreFile);
        properties.put("ssl.truststore.password", trustStorePassword);
        properties.put("ssl.keystore.location", keyStoreFile);
        properties.put("ssl.keystore.password", keyStorePassword);
        properties.put("ssl.client.auth", clientAuth);

        if (keyPassword != null && !keyPassword.isEmpty()) {
            properties.put("ssl.key.password", keyPassword);
        }

        properties.put("security.inter.broker.protocol", "SSL");
        //properties.put("inter.broker.listener.name", "SSL");

        return properties;
    }

    @Override
    public Properties getClientProperties() {
        final Properties properties = new Properties();
        properties.put("security.protocol", "SSL");
        properties.put("ssl.truststore.location", trustStoreFile);
        properties.put("ssl.truststore.password", trustStorePassword);
        properties.put("ssl.keystore.location", keyStoreFile);
        properties.put("ssl.keystore.password", keyStorePassword);

        if (keyPassword != null && !keyPassword.isEmpty()) {
            properties.put("ssl.key.password", keyPassword);
        }
        return properties;
    }
}