package net.jps.sjmx.command.jmx;

import javax.management.remote.JMXConnector;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.jmx.JMXConnectionException;
import net.jps.sjmx.jmx.JMXConnectorFactory;
import net.jps.sjmx.jmx.JMXConnectorFactoryImpl;

/**
 *
 * @author zinic
 */
public abstract class AbstractJmxCommand extends ConfigurationAwareCommand {

    private final JMXConnectorFactory connectorFactory;

    public AbstractJmxCommand(ConfigurationReader configurationReader) {
        super(configurationReader);

        connectorFactory = new JMXConnectorFactoryImpl(configurationReader);
    }

    protected JMXConnector connect() throws ConfigurationException, JMXConnectionException {
        return connectorFactory.newConnector();
    }
}
