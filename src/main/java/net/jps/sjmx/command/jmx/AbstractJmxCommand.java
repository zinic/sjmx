package net.jps.sjmx.command.jmx;

import javax.management.remote.JMXConnector;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.config.model.Configuration;
import net.jps.sjmx.config.model.Reference;
import net.jps.sjmx.config.model.SJMXConnector;
import net.jps.sjmx.jmx.JMXConnectionException;
import net.jps.sjmx.jmx.JMXConnectorFactory;
import net.jps.sjmx.jmx.JMXConnectorFactoryImpl;

/**
 *
 * @author zinic
 */
public abstract class AbstractJmxCommand extends ConfigurationAwareCommand {

    public AbstractJmxCommand(ConfigurationReader configurationReader) {
        super(configurationReader);
    }

    private static JMXConnectorFactory newConnectorFactory(ConfigurationReader configurationReader) throws ConfigurationException {
        final Configuration configuration = configurationReader.readConfiguration().getConfiguration();

        if (configuration.getCurrentConnector() == null) {
            throw new ConfigurationException("Not currently using a remote connection. Please set the current connection with \"remote use\"");
        }

        final Reference currentConnection = configuration.getCurrentConnector();

        for (SJMXConnector sjmxConnector : configuration.getSjmxConnectors().getConnector()) {
            if (currentConnection.getRefId().equals(sjmxConnector.getId())) {
                return new JMXConnectorFactoryImpl(sjmxConnector);
            }
        }

        throw new ConfigurationException("Unable to locate a remote endpoint that matches the in-use remote. Your configuration may be corrupted.");
    }

    protected JMXConnectorFactory currentJmxRemote() throws ConfigurationException, JMXConnectionException {
        return newConnectorFactory(getConfigurationReader());
    }
}
