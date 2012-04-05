package net.jps.sjmx.command.config.remote;

import java.util.Iterator;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationHandler;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.config.model.Configuration;
import net.jps.sjmx.config.model.Reference;
import net.jps.sjmx.config.model.SJMXConnector;

/**
 *
 * @author zinic
 */
public class Use extends ConfigurationAwareCommand {

   public Use(ConfigurationReader configurationManager) {
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
   public CommandResult perform(String[] arguments) throws ConfigurationException {
      if (arguments.length > 1) {
         return new InvalidArguments("Arguments expected: <remote-name>");
      }

      final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();
      final Configuration cfg = cfgHandler.getConfiguration();

         if (arguments.length == 0) {
            final Reference ref = cfg.getCurrentConnector();

            return new MessageResult("Currently using: " + (ref == null ? "" : ref.getRefId()));
         } else {
            for (Iterator<SJMXConnector> connectorIterator = cfgHandler.connectorList().iterator(); connectorIterator.hasNext();) {
               final SJMXConnector connector = connectorIterator.next();

               if (arguments[0].equals(connector.getId())) {
                  final Reference newConnectorRef = new Reference();
                  newConnectorRef.setRefId(arguments[0]);

                  cfg.setCurrentConnector(newConnectorRef);
                  cfgHandler.write();

                  return new MessageResult("Now using remote: " + arguments[0]);
               }
            }
      }

      return new MessageResult("Remote: " + arguments[0] + " not found.");
   }
}
