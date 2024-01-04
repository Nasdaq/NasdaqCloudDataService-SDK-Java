package com.nasdaq.ncdsclient.internal.utils;

import java.util.Properties;

/**
 * Utility to load the auth configuration parameters.
 */
public class AuthenticationConfigLoader {
    public static String OAUTH_TOKEN_ENDPOINT_URI = ConfigConstants.STRIMZI_OAUTH_TOKEN_ENDPOINT_URI;
    public static String OAUTH_CLIENT_ID = ConfigConstants.STRIMZI_OAUTH_CLIENT_ID;
    public static String OAUTH_CLIENT_SECRET = ConfigConstants.STRIMZI_OAUTH_CLIENT_SECRET;
    public static String OAUTH_USERNAME_CLAIM="oauth.username.claim";

    public static String getClientID(){
        return "unit-test";
    }

    public static String getClientID(Properties cfg){
        try {
           if(!IsItJunit.isJUnitTest()){
                return ConfigConstants.getPropertyOrEnv(cfg, ConfigConstants.NCDS_CLIENT_ID, ConfigConstants.ENV_OAUTH_CLIENT_ID);
            }
            else {
                return "unit-test";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean validateSecurityConfig(Properties cfg, Properties kafkaCfg) throws Exception {
        if (cfg.getProperty(OAUTH_TOKEN_ENDPOINT_URI) == null && cfg.getProperty(ConfigConstants.OAUTH_TOKEN_ENDPOINT_URI) == null ) {
          throw new Exception(String.format("Authentication Setting : %s and %s (preferred) Missing", OAUTH_TOKEN_ENDPOINT_URI, ConfigConstants.OAUTH_TOKEN_ENDPOINT_URI) );
        }
        final String jaasConfig = cfg.getProperty(ConfigConstants.JAAS_CONFIG);
        if (jaasConfig == null) {
            if (ConfigConstants.getPropertyOrEnv(cfg, OAUTH_CLIENT_ID, ConfigConstants.ENV_OAUTH_CLIENT_ID) == null) {
                throw new Exception ("Authentication Setting :" + OAUTH_CLIENT_ID  + " Missing" );
            }
            if (ConfigConstants.getPropertyOrEnv(cfg, OAUTH_CLIENT_SECRET, ConfigConstants.ENV_OAUTH_CLIENT_SECRET) == null) {
                throw new Exception("Authentication Setting :" + OAUTH_CLIENT_SECRET  + " Missing" );
            }
        }
        return true;
    }

}
