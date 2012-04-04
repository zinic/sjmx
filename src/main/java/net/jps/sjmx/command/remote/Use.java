package net.jps.sjmx.command.remote;

import java.util.Iterator;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationManager;
import net.jps.sjmx.config.model.Configuration;
import net.jps.sjmx.config.model.Reference;
import net.jps.sjmx.config.model.SJMXConnector;

/**
 *
 * @author zinic
 */
public class Use extends ConfigurationAwareCommand {

    public Use(ConfigurationManager configurationManager) {
        super(configurationManager);
    }

    @Override
    public String getCommandDescription() {
        return "use [remote-name]";
    }

    @Override
    public String getCommandToken() {
        return "use";
    }

    @Override
    public CommandResult perform(String[] arguments) {
        if (arguments.length > 1) {
            return new InvalidArguments("Arguments expected: <remote-name>");
        }

        try {
            final Configuration cfg = getConfigurationManager().get();

            if (cfg.getSjmxConnectors() != null) {
                if (arguments.length == 0) {
                    final Reference ref = cfg.getCurrentConnector();
                    
                    return new MessageResult("Currently using: " + (ref == null ? "" : ref.getRefId()));
                } else {
                    for (Iterator<SJMXConnector> connectorIterator = cfg.getSjmxConnectors().getConnector().iterator(); connectorIterator.hasNext();) {
                        final SJMXConnector connector = connectorIterator.next();

                        if (arguments[0].equals(connector.getId())) {
                            final Reference newConnectorRef = new Reference();
                            newConnectorRef.setRefId(arguments[0]);
                            
                            cfg.setCurrentConnector(newConnectorRef);
                            getConfigurationManager().write(cfg);

                            return new MessageResult("Now using remote: " + arguments[0]);
                        }
                    }
                }
            }

            return new MessageResult("Remote: " + arguments[0] + " not found.");
        } catch (ConfigurationException ce) {
            return new ExceptionResult(ce);
        }
    }
}
