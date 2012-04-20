package net.jps.sjmx.jmx;

import javax.management.remote.JMXConnector;
import net.jps.sjmx.config.model.SJMXConnector;

/**
 *
 * @author zinic
 */
public class JMXConnectorFactoryImpl implements JMXConnectorFactory {

    private final SJMXConnector sJMXConnector;

    public JMXConnectorFactoryImpl(SJMXConnector sJMXConnector) {
        this.sJMXConnector = sJMXConnector;
    }

   
    @Override
    public JMXConnector newConnector() throws JMXConnectionException {

        return new JMXConnection(sJMXConnector).connect();
    }
}
