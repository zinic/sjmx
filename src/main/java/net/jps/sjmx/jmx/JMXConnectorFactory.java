package net.jps.sjmx.jmx;

import javax.management.remote.JMXConnector;

/**
 *
 * @author zinic
 */
public interface JMXConnectorFactory {

    JMXConnector newConnector() throws JMXConnectionException;
}
