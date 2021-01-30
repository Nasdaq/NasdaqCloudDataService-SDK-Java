package com.nasdaq.ncdsclient.utils;

import com.salesforce.kafka.test.listeners.AbstractListener;

import java.util.Properties;

public class PlainListenerSpecificPort extends AbstractListener<PlainListenerSpecificPort>{
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

    @Override
    public PlainListenerSpecificPort onPorts(int... ports) {
        return super.onPorts(9095);
    }

    @Override
    public int getNextPort() {
        return 9095;
    }
}
