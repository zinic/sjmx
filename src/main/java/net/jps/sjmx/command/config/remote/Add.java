package net.jps.sjmx.command.config.remote;

import java.math.BigInteger;
import java.util.Iterator;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationHandler;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.config.model.*;

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
         final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();

         if (cfgHandler.findConnector(arguments[0]) != null) {
            return new CommandFailure("Remote connection " + arguments[0] + " already exists.");
         }

         return addSjmxConnector(cfgHandler, arguments[0], arguments[1]);
      } catch (ConfigurationException ce) {
         return new ExceptionResult(ce);
      }
   }

   private CommandResult addSjmxConnector(ConfigurationHandler cfgHandler, String remoteName, String connectionString) throws ConfigurationException {
      final Configuration cfg = cfgHandler.getConfiguration();
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

      cfgHandler.connectorList().add(connector);
      cfgHandler.write();

      return new SuccessResult();
   }
}
