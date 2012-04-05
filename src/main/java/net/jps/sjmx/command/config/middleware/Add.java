package net.jps.sjmx.command.config.middleware;

import net.jps.sjmx.cli.command.result.CommandResult;
import net.jps.sjmx.cli.command.result.MessageResult;
import net.jps.sjmx.cli.command.result.SuccessResult;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationHandler;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.config.model.MiddlewarePipeline;
import net.jps.sjmx.config.model.MiddlewareReference;
import net.jps.sjmx.config.model.Reference;
import net.jps.sjmx.config.model.SJMXConnector;

/**
 *
 * @author zinic
 */
public class Add extends ConfigurationAwareCommand {

   public Add(ConfigurationReader configurationManager) {
      super(configurationManager);
   }

   @Override
   public String getCommandDescription() {
      return "Adds a python JMX middleware to a remote location.";
   }

   @Override
   public String getCommandToken() {
      return "add";
   }

   @Override
   public CommandResult perform(String[] arguments) throws ConfigurationException {
      if (arguments.length != 2) {
         return new MessageResult("Expecting a python filename and a python classname.");
      }
      
      final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();
      final SJMXConnector cuurentConnector = cfgHandler.currentConnector();
      
      if (cuurentConnector == null) {
         return new MessageResult("Must have a remote location selected before adding a middleware pipeline.");
      }

      MiddlewarePipeline pipeline = cuurentConnector.getPipeline();

      if (pipeline == null) {
         pipeline = new MiddlewarePipeline();
         cuurentConnector.setPipeline(pipeline);
      }
      
      final MiddlewareReference filterReference = new MiddlewareReference();
      filterReference.setHref(arguments[0]);
      filterReference.setClassName(arguments[1]);
      
      pipeline.getFilter().add(filterReference);
      cfgHandler.write();
      
      return new SuccessResult();
   }
}
