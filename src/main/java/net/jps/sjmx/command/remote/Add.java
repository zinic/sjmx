package net.jps.sjmx.command.remote;

import java.math.BigInteger;
import java.util.Iterator;
import net.jps.sjmx.cli.command.AbstractCommand;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationManager;
import net.jps.sjmx.config.model.*;

/**
 *
 * @author zinic
 */
public class Add extends ConfigurationAwareCommand {
    
    public Add(ConfigurationManager configurationManager) {
        super(configurationManager);
    }
    
    @Override
    public String getCommandDescription() {
        return "add <remote-name> [username:password@]<host:port>";
    }
    
    @Override
    public String getCommandToken() {
        return "add";
    }
    
    @Override
    public CommandResult perform(String[] arguments) {
        if (arguments.length != 2) {
            return new InvalidArguments("Arguments expected: <remote-name> [username:password@]<host:port>");
        }
        
        try {
            final Configuration cfg = getConfigurationManager().get();
            
            if (cfg.getSjmxConnectors() != null) {
                for (Iterator<SJMXConnector> connectorIterator = cfg.getSjmxConnectors().getConnector().iterator(); connectorIterator.hasNext();) {
                    final SJMXConnector connector = connectorIterator.next();
                    
                    if (arguments[0].equals(connector.getId())) {
                        return new CommandFailure("Remote connection " + arguments[0] + " already exists.");
                    }
                }
            } else {
                cfg.setSjmxConnectors(new SJMXConnectorList());
            }
            
            return addSjmxConnector(cfg, arguments[0], arguments[1]);
        } catch (ConfigurationException ce) {
            return new ExceptionResult(ce);
        }
    }
    
    private CommandResult addSjmxConnector(Configuration cfg, String remoteName, String connectionString) {
        final String[] usernameSplit = connectionString.split("@", 2);
        JMXCredentials credentials = null;
        
        if (usernameSplit.length > 1) {
            final String[] usernamePasswordSplit = usernameSplit[0].split(":", 2);
            
            if (usernamePasswordSplit.length == 2) {
                credentials = new JMXCredentials();
                credentials.setUsername(usernamePasswordSplit[0]);
                credentials.setPassword(usernamePasswordSplit[1]);
            }
        }
        
        final String[] hostPortSplit = usernameSplit[usernameSplit.length - 1].split(":", 2);
        
        if (hostPortSplit.length == 1) {
            return new CommandFailure("Port must be specified.");
        }
        
        try {
            final int hostPort = Integer.parseInt(hostPortSplit[1]);
            
            final SJMXConnector connector = new SJMXConnector();
            connector.setId(remoteName);
            connector.setHost(hostPortSplit[0]);
            connector.setPort(BigInteger.valueOf(hostPort));
            
            if (credentials != null) {
                connector.setCredentials(credentials);
            }
            
            if (cfg.getCurrentConnector() == null) {
                final Reference ref = new Reference();
                ref.setRefId(remoteName);
                
                cfg.setCurrentConnector(ref);
            }
            
            cfg.getSjmxConnectors().getConnector().add(connector);
            
            getConfigurationManager().write(cfg);
        } catch (NumberFormatException nfe) {
            return new ExceptionResult(nfe);
        } catch (ConfigurationException ce) {
            return new ExceptionResult(ce);
        }
        
        return new SuccessResult();
    }
}
