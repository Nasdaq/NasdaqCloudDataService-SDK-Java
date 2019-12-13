package com.nasdaq.ncdsclient.exceptions;

import java.io.IOException;
import java.nio.file.Path;


/**
 * This is a Exception class for reading properties
 *
 * @author rucvan
 */
public class KafkaPropertiesException extends IOException {

    private final Path path;

    /**
     * Constructs a exception with given path.
     * @param  path path from where file is being read
     */
    public KafkaPropertiesException(Path path)
    {
        this(path, null, null);
    }

    /**
     * Constructs a exception with given path and message to display.
     * @param  path path from where file is being read
     * @param  msg String message to be display
     */
    public KafkaPropertiesException(Path path,String msg)
    {
        this(path, msg, null);
    }

    /**
     * Constructs a exception with given path and message to display.
     * @param  path path from where file is being read
     * @param  msg String message to be display
     * @param  cause pass the cause of the exception
     */
    public KafkaPropertiesException(Path path,
                             String msg,
                             Throwable cause)
    {
        super(msg, cause);
        this.path = path;
    }

    /**
     * Get the path of config file where exception has happened
     * @return  path path from where file is being read
     */
    public Path getPath() {
        return path;
    }
}
