package net.jps.sjmx.jmx;

/**
 *
 * @author zinic
 */
public class JMXConnectionException extends Exception {

    public JMXConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public JMXConnectionException(String message) {
        super(message);
    }
}
