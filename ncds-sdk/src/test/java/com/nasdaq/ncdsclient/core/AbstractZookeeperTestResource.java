package com.nasdaq.ncdsclient.core;

public abstract class AbstractZookeeperTestResource {
    /**
     * Internal Zookeeper test server instance.
     */
    private final ZookeeperTestServer zookeeperTestServer = new ZookeeperTestServer();

    /**
     * Access to the underlying zookeeper test server instance.
     * @return Shared Zookeeper test server instance.
     * @throws IllegalStateException if before() has not been called yet.
     */
    public ZookeeperTestServer getZookeeperTestServer() throws IllegalStateException {
        return zookeeperTestServer;
    }

    /**
     * Returns connection string for zookeeper clients.
     * @return Connection string to connect to the Zookeeper instance.
     * @throws IllegalStateException if before() has not been called yet.
     */
    public String getZookeeperConnectString() throws IllegalStateException {
        return zookeeperTestServer.getConnectString();
    }
}
