package net.jps.sjmx.command.config.remote;

import java.util.Iterator;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationHandler;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.config.model.Configuration;
import net.jps.sjmx.config.model.SJMXConnector;

/**
 *
 * @author zinic
 */
public class Remove extends ConfigurationAwareCommand {

   public Remove(ConfigurationReader configurationManager) {
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
   public CommandResult perform(String[] arguments) throws ConfigurationException {
      if (arguments.length != 1) {
         return new InvalidArguments("Arguments expected: <remote-name>");
      }

      final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();
      final Configuration cfg = cfgHandler.getConfiguration();

      for (Iterator<SJMXConnector> connectorIterator = cfgHandler.connectorList().iterator(); connectorIterator.hasNext();) {
         final SJMXConnector connector = connectorIterator.next();

         if (arguments[0].equals(connector.getId())) {
            connectorIterator.remove();

            if (cfg.getCurrentConnector() != null && arguments[0].equals(cfg.getCurrentConnector().getRefId())) {
               cfg.setCurrentConnector(null);
            }

            cfgHandler.write();

            return new SuccessResult();
         }
      }

      return new MessageResult("Remote: " + arguments[0] + " not found.");
   }
}
