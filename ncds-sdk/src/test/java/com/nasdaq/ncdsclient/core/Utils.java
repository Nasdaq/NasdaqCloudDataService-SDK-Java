package com.nasdaq.ncdsclient.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Collection of Utilities.
 */
class Utils {
    /**
     * Create a temporary directory on disk that will be cleaned up when the process terminates.
     * @return Absolute path to the temporary directory.
     */
    static File createTempDirectory() {
        // Create temp path to store logs
        final File logDir;
        try {
            logDir = Files.createTempDirectory("kafka-unit").toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Ensure its removed on termination.
        logDir.deleteOnExit();

        return logDir;
    }
}
