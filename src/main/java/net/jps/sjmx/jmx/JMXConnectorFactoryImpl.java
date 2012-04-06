package net.jps.sjmx.jmx;

import javax.management.remote.JMXConnector;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.config.model.Configuration;
import net.jps.sjmx.config.model.Reference;
import net.jps.sjmx.config.model.SJMXConnector;

/**
 *
 * @author zinic
 */
public class JMXConnectorFactoryImpl implements JMXConnectorFactory {

    private final ConfigurationReader configurationReader;

    public JMXConnectorFactoryImpl(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }

    @Override
    public JMXConnector newConnector() throws ConfigurationException, JMXConnectionException {
        final Configuration configuration = configurationReader.readConfiguration().getConfiguration();

        if (configuration.getCurrentConnector() == null) {
            throw new ConfigurationException("Not currently using a remote connection. Please set the current connection with \"remote use\"");
        }

        final Reference currentConnection = configuration.getCurrentConnector();

        for (SJMXConnector sjmxConnector : configuration.getSjmxConnectors().getConnector()) {
            if (currentConnection.getRefId().equals(sjmxConnector.getId())) {
                return new JMXConnection(sjmxConnector).connect();
            }
        }

        throw new ConfigurationException("Unable to locate a remote endpoint that matches the in-use remote. Your configuration may be corrupted.");
    }
}
