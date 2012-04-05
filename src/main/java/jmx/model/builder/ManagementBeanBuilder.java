/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jmx.model.builder;

import jmx.model.info.ManagementBeanInfo;

/**
 *
 * @author zinic
 */
public interface ManagementBeanBuilder {

   void alias(ManagementBeanInfo monitorInfo, String attributeName, String alias);

   void setDescription(String description);

   void setName(String name);

   void setType(String type);
   
}
