/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.jps.sjmx.config;

import net.jps.sjmx.config.model.Configuration;

/**
 *
 * @author zinic
 */
public interface ConfigurationManager {

    Configuration get() throws ConfigurationException;

    void write(Configuration configuration) throws ConfigurationException;
    
}
