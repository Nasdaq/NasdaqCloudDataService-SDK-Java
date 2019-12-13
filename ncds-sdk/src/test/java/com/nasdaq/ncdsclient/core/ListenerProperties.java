package com.nasdaq.ncdsclient.core;

import java.util.Properties;

/**
 * Defines properties about a registered listener.
 */
public class ListenerProperties {
    private final String protocol;
    private final String connectString;
    private final Properties clientProperties;

    /**
     * Constructor.
     * @param protocol protocol of the listener.
     * @param connectString Connect string for listener.
     * @param clientProperties Any client properties required to connect.
     */
    public ListenerProperties(final String protocol, final String connectString, final Properties clientProperties) {
        this.protocol = protocol;
        this.connectString = connectString;
        this.clientProperties = clientProperties;
    }

    /**
     * Getter.
     * @return Name of protocol the listener is registered on.
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Getter.
     * @return Connect string for talking to this listenre.
     */
    public String getConnectString() {
        return connectString;
    }

    /**
     * Getter.
     * @return Any Kafka client properties that need to be set to talk to this listener.
     */
    public Properties getClientProperties() {
        // Return a copy of the properties.
        final Properties copy = new Properties();
        copy.putAll(clientProperties);
        return copy;
    }
}
