package com.nasdaq.ncdsclient.listeners;

import java.util.Properties;

/**
 * Default implementation.  Defines a PLAINTEXT listener.
 */
public class PlainListener implements BrokerListener {

    @Override
    public String getProtocol() {
        return "PLAINTEXT";
    }

    @Override
    public Properties getBrokerProperties() {
        return new Properties();
    }

    @Override
    public Properties getClientProperties() {
        return new Properties();
    }
}
