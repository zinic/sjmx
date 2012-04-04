package net.jps.sjmx.config;

/**
 *
 * @author zinic
 */
public class ConfigurationException extends Exception {

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(String message) {
        super(message);
    }
}
