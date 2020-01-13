package com.nasdaq.ncdsclient.internal.utils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Utility to load the auth configuration parameters.
 */
public class AuthenticationConfigLoader {
    public static String OAUTH_TOKEN_ENDPOINT_URI ="oauth.token.endpoint.uri";
    public static String OAUTH_CLIENT_ID ="oauth.client.id";
    public static String OAUTH_CLIENT_SECRET="oauth.client.secret";
    public static String OAUTH_USERNAME_CLAIM="oauth.username.claim";
    public static String JAVAX_NET_SSL_TRUSTSTORE="javax.net.ssl.trustStore";
    public static String JAVAX_NET_SSL_TRUSTSTOREPASSWORD="javax.net.ssl.trustStorePassword";
    public static String JAVAX_NET_SSL_TRUSTSTORETYPE="javax.net.ssl.trustStoreType";

    public static String getClientID(){
        String clientID;
        try {
            // Just for the unit test
            Properties cfg = new Properties();
            cfg.setProperty(OAUTH_CLIENT_ID, "unit-test");

            if(!IsItJunit.isJUnitTest()){
                clientID = cfg.getProperty(OAUTH_CLIENT_ID);
            }
            else {
                clientID = "unit-test";
            }
        return clientID;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getClientID(Properties cfg){
        String clientID;
        try {
           if(!IsItJunit.isJUnitTest()){
                clientID = cfg.getProperty(OAUTH_CLIENT_ID);
            }
            else {
                clientID = "unit-test";
            }
            return clientID;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean validateSecurityConfig(Properties cfg) throws Exception {

        if (cfg.getProperty(OAUTH_TOKEN_ENDPOINT_URI) == null) {
          throw new Exception ("Authentication Setting :" + OAUTH_TOKEN_ENDPOINT_URI  + " Missing" );
        }
        if (cfg.getProperty(OAUTH_CLIENT_ID) == null && System.getenv("OAUTH_CLIENT_ID") == null ) {
            throw new Exception ("Authentication Setting :" + OAUTH_CLIENT_ID  + " Missing" );
        }
        if (cfg.getProperty(OAUTH_CLIENT_SECRET) == null && System.getenv("OAUTH_CLIENT_SECRET") == null) {
            throw new Exception("Authentication Setting :" + OAUTH_CLIENT_SECRET  + " Missing" );
        }
        if (cfg.getProperty(OAUTH_USERNAME_CLAIM) == null) {
            throw new Exception("Authentication Setting :" + OAUTH_USERNAME_CLAIM  + " Missing" );
        }
        if (cfg.getProperty(JAVAX_NET_SSL_TRUSTSTORE) == null && System.getenv("JAVAX_NET_SSL_TRUSTSTORE") == null) {
            throw new Exception("Authentication Setting :" + JAVAX_NET_SSL_TRUSTSTORE  + " Missing" );
        }
        if (cfg.getProperty(JAVAX_NET_SSL_TRUSTSTOREPASSWORD) == null && System.getenv("JAVAX_NET_SSL_TRUSTSTOREPASSWORD") == null) {
            throw new Exception ("Authentication Setting :" + JAVAX_NET_SSL_TRUSTSTOREPASSWORD  + " Missing" );
        }
        if (cfg.getProperty(JAVAX_NET_SSL_TRUSTSTORETYPE) == null) {
            throw new Exception ("Authentication Setting :" + JAVAX_NET_SSL_TRUSTSTORETYPE  + " Missing" );
        }

        return true;
    }
}
