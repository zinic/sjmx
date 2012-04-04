package net.jps.sjmx.command.remote;

import net.jps.sjmx.cli.command.AbstractCommand;
import net.jps.sjmx.cli.command.result.CommandResult;
import net.jps.sjmx.cli.command.result.ExceptionResult;
import net.jps.sjmx.cli.command.result.MessageResult;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationManager;
import net.jps.sjmx.config.model.Configuration;
import net.jps.sjmx.config.model.SJMXConnector;

/**
 *
 * @author zinic
 */
public class List extends ConfigurationAwareCommand {

    public List(ConfigurationManager configurationManager) {
        super(configurationManager);
    }

    @Override
    public CommandResult perform(String[] arguments) {
        try {
            final Configuration cfg = getConfigurationManager().get();
            final StringBuilder builder = new StringBuilder();
            
            if (cfg.getSjmxConnectors() != null) {
                for (SJMXConnector connector : cfg.getSjmxConnectors().getConnector()) {
                    builder.append(connector.getId()).append("\n");
                }
            }
            
            return new MessageResult(builder.toString());
        } catch(ConfigurationException ce) {
            return new ExceptionResult(ce);
        }
    }

    @Override
    public String getCommandDescription() {
        return "lists remote known connectors";
    }

    @Override
    public String getCommandToken() {
        return "list";
    }
}
