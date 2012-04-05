package net.jps.sjmx.command.config.middleware;

import net.jps.sjmx.cli.command.result.CommandResult;
import net.jps.sjmx.cli.command.result.MessageResult;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationHandler;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.config.model.MiddlewareReference;
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
   public String getCommandDescription() {
      return "Lists the python JMX middleware pipeline.";
   }

   @Override
   public String getCommandToken() {
      return "list";
   }

   @Override
   public CommandResult perform(String[] arguments) throws ConfigurationException {
      final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();
      final SJMXConnector cuurentConnector = cfgHandler.currentConnector();

      if (cuurentConnector == null) {
         return new MessageResult("Must have a remote location selected before adding a middleware pipeline.");
      }

      final StringBuilder stringBuilder = new StringBuilder();

      if (cuurentConnector.getPipeline() != null) {
         int i = 0;
         
         for (MiddlewareReference ref : cuurentConnector.getPipeline().getFilter()) {
            i += 1;
            
            stringBuilder.append(i).append(": ").append(ref.getClassName()).append(" from ").append(ref.getHref());
         }
      }

      return new MessageResult(stringBuilder.toString());
   }
}
