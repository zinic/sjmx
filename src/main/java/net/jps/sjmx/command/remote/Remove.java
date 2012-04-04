package net.jps.sjmx.command.remote;

import java.util.Iterator;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationManager;
import net.jps.sjmx.config.model.Configuration;
import net.jps.sjmx.config.model.SJMXConnector;

/**
 *
 * @author zinic
 */
public class Remove extends ConfigurationAwareCommand {

    public Remove(ConfigurationManager configurationManager) {
        super(configurationManager);
    }

    @Override
    public String getCommandDescription() {
        return "remove <remote-name>";
    }

    @Override
    public String getCommandToken() {
        return "remove";
    }

    @Override
    public CommandResult perform(String[] arguments) {
        if (arguments.length != 1) {
            return new InvalidArguments("Arguments expected: <remote-name>");
        }

        try {
            final Configuration cfg = getConfigurationManager().get();

            if (cfg.getSjmxConnectors() != null) {
                for (Iterator<SJMXConnector> connectorIterator = cfg.getSjmxConnectors().getConnector().iterator(); connectorIterator.hasNext();) {
                    final SJMXConnector connector = connectorIterator.next();

                    if (arguments[0].equals(connector.getId())) {
                        connectorIterator.remove();
                        
                        if (cfg.getCurrentConnector() != null && arguments[0].equals(cfg.getCurrentConnector().getRefId())) {
                            cfg.setCurrentConnector(null);
                        }
                        
                        getConfigurationManager().write(cfg);
                        
                        return new SuccessResult();
                    }
                }
            }

            return new MessageResult("Remote: " + arguments[0] + " not found.");
        } catch (ConfigurationException ce) {
            return new ExceptionResult(ce);
        }
    }
}
