package com.nasdaq.ncdsclient.internal.utils;

import com.nasdaq.ncdsclient.exceptions.KafkaPropertiesException;

import java.io.InputStream;
import java.util.*;

/**
 * Utility to load the kafka configuration parameters.
 */
public class KafkaConfigLoader {
    public static String BOOTSTRAP_SERVERS="bootstrap.servers";
    public static Properties loadConfig() throws Exception {
        Properties cfg = new Properties();
        InputStream inputStream;
        try {
                inputStream = ClassLoader.getSystemResourceAsStream("junit-config.properties");
                if (inputStream == null) {
                    System.out.println("kafka-config.properties: Unable to produce input Stream ");
                    throw new Exception ("kafka-config.properties: Unable to produce input Stream ");
            }
            cfg.load(inputStream);
        }
        catch (Exception e) {
            throw e;
        }
        nasdaqSepecificConfig(cfg);
        return cfg;
    }

    private static Properties nasdaqSepecificConfig(Properties p) throws KafkaPropertiesException{
        //Properties p = new Properties();
        if(!IsItJunit.isJUnitTest()) {
            p.setProperty("security.protocol", "SASL_SSL");
            p.setProperty("sasl.mechanism", "OAUTHBEARER");
            p.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required ;");
            p.setProperty("sasl.login.callback.handler.class", "io.strimzi.kafka.oauth.client.JaasClientOauthLoginCallbackHandler");
            p.setProperty("ssl.endpoint.identification.algorithm","");
        }
        return p;
    }

    public static Properties validateAndAddSpecificProperties(Properties p) throws Exception {
        if (p.getProperty(BOOTSTRAP_SERVERS) == null) {
            throw new Exception ("bootstrap.servers  Properties is not set in the Kafka Configuration ");
        }
        nasdaqSepecificConfig(p);
        return p;
    }
 }
