package com.nasdaq.ncdsclient.internal.utils;

import com.nasdaq.ncdsclient.exceptions.KafkaPropertiesException;

import java.io.InputStream;
import java.util.*;

/**
 * Utility to load the kafka configuration parameters.
 */
public class KafkaConfigLoader {
    public static final String BOOTSTRAP_SERVERS = "bootstrap.servers";
    public static final String SSL_TRUSTSTORE_TYPE = "ssl.truststore.type";
    public static final String SSL_TRUSTSTORE_LOCATION = "ssl.truststore.location";
    public static final String SSL_TRUSTSTORE_PASSWORD = "ssl.truststore.password";

    public static Properties loadConfig() throws Exception {
        Properties cfg = new Properties();
        InputStream inputStream;
        try {
                inputStream = ClassLoader.getSystemResourceAsStream("junit-config.properties");
                if (inputStream == null) {
                    System.out.println("junit-config.properties: Unable to produce input Stream ");
                    throw new Exception ("junit-config.properties: Unable to produce input Stream ");
            }
            cfg.load(inputStream);
        }
        catch (Exception e) {
            throw e;
        }
        nasdaqSpecificConfig(cfg);
        return cfg;
    }

    private static Properties nasdaqSpecificConfig(Properties p) throws KafkaPropertiesException{
        //Properties p = new Properties();
        if(!IsItJunit.isJUnitTest()) {
            ConfigConstants.addDefaults(p);
            ConfigConstants.addJaasConfig(p);
            p.remove(AuthenticationConfigLoader.OAUTH_USERNAME_CLAIM);
        }
        return p;
    }

    public static Properties validateAndAddSpecificProperties(Properties p) throws Exception {
        if (p.getProperty(BOOTSTRAP_SERVERS) == null) {
            throw new Exception ("bootstrap.servers  Properties is not set in the Kafka Configuration ");
        }
        nasdaqSpecificConfig(p);
        return p;
    }
 }
