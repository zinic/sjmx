/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jps.sjmx.jmx;

import javax.management.remote.JMXConnector;
import net.jps.sjmx.config.ConfigurationException;

/**
 *
 * @author zinic
 */
public interface JMXConnectorFactory {

    JMXConnector newConnector() throws ConfigurationException, JMXConnectionException;
    
}
