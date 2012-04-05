package net.jps.sjmx.config;

import net.jps.sjmx.config.model.Configuration;

/**
 *
 * @author zinic
 */
public interface ConfigurationWriter {

   void write(Configuration configuration) throws ConfigurationException;
}
