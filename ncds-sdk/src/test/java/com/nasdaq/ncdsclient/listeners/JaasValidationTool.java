package com.nasdaq.ncdsclient.listeners;

/**
 * Simple utility class to determine if the JAAS system environment variable has been set.
 */
class JaasValidationTool {
    public static final String JAAS_VARIABLE_NAME = "java.security.auth.login.config";

    /**
     * Is the JAAS environment variable set.
     * @return true if set, false if not.
     */
    public static boolean isJaasEnvironmentValueSet() {
        return System.getProperty(JAAS_VARIABLE_NAME) != null;
    }
}
