package com.nasdaq.ncdsclient.internal.utils;

import java.util.Properties;

/**
 *
 */
public final class ConfigConstants {

    public static final String STRIMZI_OAUTH_TOKEN_ENDPOINT_URI = "oauth.token.endpoint.uri";
    public static final String STRIMZI_OAUTH_CLIENT_ID = "oauth.client.id";
    public static final String STRIMZI_OAUTH_CLIENT_SECRET = "oauth.client.secret";
    public static final String OAUTH_TOKEN_ENDPOINT_URI = "sasl.oauthbearer.token.endpoint.url";
    public static final String JAAS_CONFIG = "sasl.jaas.config";
    public static final String CONFLUENT_CLUSTER_ID_CONFIG = "confluent.cluster.id";
    public static final String CONFLUENT_IDENTITY_POOL_ID = "confluent.identity.pool.id";
    public static final String ENV_OAUTH_CLIENT_ID = "OAUTH_CLIENT_ID";
    public static final String ENV_OAUTH_CLIENT_SECRET = "OAUTH_CLIENT_SECRET";
    public static final String NCDS_CLIENT_ID = "ncds.client.id";

    private static final String SASL_LOGIN_CALLBACK_HANDLER_CLASS = "sasl.login.callback.handler.class";
    private static final String SASL_MECHANISM = "sasl.mechanism";
    private static final String SECURITY_PROTOCOL = "security.protocol";
    public static final String JAAS_CONFIG_VALUE = "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required clientId='%s' clientSecret='%s' extension_logicalCluster='%s' extension_identityPoolId='%s';";

    private static final Properties defaultProperties = defaultClusterProperties();

    private ConfigConstants() {
    }

    public static final String getPropertyOrEnv(Properties props, String propsKey, String envKey) {
        String property = props.getProperty(propsKey);
        if (property != null) {
            return property;
        }
        property = System.getProperty(propsKey);
        if (property != null) {
            return property;
        }
        return System.getenv(envKey);
    }

    public static final void addDefaults(Properties props) {
        addDefaults(props, SECURITY_PROTOCOL);
        addDefaults(props, SASL_MECHANISM);
        addDefaults(props, SASL_LOGIN_CALLBACK_HANDLER_CLASS);
    }

    private static final void addDefaults(Properties props, String propsKey) {
        if (props.getProperty(propsKey) == null) {
            props.setProperty(propsKey, defaultProperties.getProperty(propsKey));
        }
    }

    private static final Properties defaultClusterProperties() {
        final Properties p = new Properties();
        p.setProperty(SECURITY_PROTOCOL, "SASL_SSL");
        p.setProperty(SASL_MECHANISM, "OAUTHBEARER");
        p.setProperty(SASL_LOGIN_CALLBACK_HANDLER_CLASS, "org.apache.kafka.common.security.oauthbearer.secured.OAuthBearerLoginCallbackHandler");
        return p;
    }

    public static final void addJaasConfig(Properties source) {
        final String akUri = source.getProperty(OAUTH_TOKEN_ENDPOINT_URI);
        if (akUri == null) {
            final String strimziUri = source.getProperty(STRIMZI_OAUTH_TOKEN_ENDPOINT_URI);
            source.remove(STRIMZI_OAUTH_TOKEN_ENDPOINT_URI);
            source.setProperty(OAUTH_TOKEN_ENDPOINT_URI, strimziUri);
        }

        final String jaasConfig = source.getProperty(JAAS_CONFIG);
        if (jaasConfig == null) {
            final String clientId = getPropertyOrEnv(source, STRIMZI_OAUTH_CLIENT_ID, ENV_OAUTH_CLIENT_ID);
            final String clientSecret = getPropertyOrEnv(source, STRIMZI_OAUTH_CLIENT_SECRET, ENV_OAUTH_CLIENT_SECRET);
            final String clusterId = source.getProperty(CONFLUENT_CLUSTER_ID_CONFIG, "dummy");
            final String identityPoolId = source.getProperty(CONFLUENT_IDENTITY_POOL_ID, "dummy");
            source.remove(STRIMZI_OAUTH_CLIENT_ID);
            source.remove(STRIMZI_OAUTH_CLIENT_SECRET);
            source.remove(CONFLUENT_CLUSTER_ID_CONFIG);
            source.remove(CONFLUENT_IDENTITY_POOL_ID);
            source.setProperty(JAAS_CONFIG, String.format(JAAS_CONFIG_VALUE, clientId, clientSecret, clusterId, identityPoolId));
            //Store client id with a non-strimzi key for use in calculating group.id, if needed
            source.setProperty(NCDS_CLIENT_ID, clientId);
        }
    }
}
