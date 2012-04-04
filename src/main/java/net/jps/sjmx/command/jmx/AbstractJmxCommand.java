package net.jps.sjmx.command.jmx;

import javax.management.remote.JMXConnector;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationManager;
import net.jps.sjmx.config.model.Configuration;
import net.jps.sjmx.config.model.Reference;
import net.jps.sjmx.config.model.SJMXConnector;
import net.jps.sjmx.jmx.JMXConnection;
import net.jps.sjmx.jmx.JMXConnectionException;

/**
 *
 * @author zinic
 */
public abstract class AbstractJmxCommand extends ConfigurationAwareCommand {

    public AbstractJmxCommand(ConfigurationManager configurationManager) {
        super(configurationManager);
    }
    
    protected JMXConnector connect() throws ConfigurationException, JMXConnectionException {
        final Configuration configuration = getConfigurationManager().get();
        
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
