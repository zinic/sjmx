package net.jps.sjmx.config;

/**
 *
 * @author zinic
 */
public interface ConfigurationReader {

   ConfigurationHandler readConfiguration() throws ConfigurationException;
}
