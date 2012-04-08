package net.jps.sjmx.config;

import java.util.List;
import net.jps.sjmx.config.model.Configuration;
import net.jps.sjmx.config.model.Reference;
import net.jps.sjmx.config.model.SJMXConnector;
import net.jps.sjmx.config.model.SJMXConnectorList;

/**
 *
 * @author zinic
 */
public class ConfigurationHandler {

   private final ConfigurationManager managerReference;
   private final Configuration configuration;

   public ConfigurationHandler(ConfigurationManager managerReference, Configuration configuration) {
      this.managerReference = managerReference;
      this.configuration = configuration;
   }

   public void write() throws ConfigurationException {
      managerReference.write(configuration);
   }
   
   public Configuration getConfiguration() {
      return configuration;
   }

   public List<SJMXConnector> connectorList() {
      SJMXConnectorList actualListContainer = configuration.getSjmxConnectors();
      
      if (actualListContainer == null) {
         actualListContainer = new SJMXConnectorList();
         configuration.setSjmxConnectors(actualListContainer);
      }

      return actualListContainer.getConnector();
   }

   public SJMXConnector findConnector(String id) throws ConfigurationException {
      for (SJMXConnector sjmxConnector : configuration.getSjmxConnectors().getConnector()) {
         if (id.equals(sjmxConnector.getId())) {
            return sjmxConnector;
         }
      }
      
      // throw new ConfigurationException("Unable to locate a remote endpoint that matches the in-use remote. Your configuration may be corrupted.");
      return null;
   }

   public SJMXConnector currentConnector() throws ConfigurationException {
      if (configuration.getCurrentConnector() == null) {
         throw new ConfigurationException("Not currently using a remote connection. Please set the current connection with \"remote use\"");
      }

      final Reference currentConnection = configuration.getCurrentConnector();

      return findConnector(currentConnection.getRefId());
   }
}
