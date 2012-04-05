package net.jps.sjmx.command.config.remote;

import net.jps.sjmx.cli.command.result.CommandResult;
import net.jps.sjmx.cli.command.result.MessageResult;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationHandler;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.config.model.SJMXConnector;

/**
 *
 * @author zinic
 */
public class List extends ConfigurationAwareCommand {

   public List(ConfigurationReader configurationManager) {
      super(configurationManager);
   }

   @Override
   public CommandResult perform(String[] arguments) throws ConfigurationException {
      final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();
      final StringBuilder builder = new StringBuilder();

      for (SJMXConnector connector : cfgHandler.connectorList()) {
         builder.append(connector.getId()).append("\n");
      }

      return new MessageResult(builder.toString());
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
