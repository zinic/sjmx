package sjmx.filter;

import jmx.model.builder.ManagementBeanBuilder;
import jmx.model.info.ManagementBeanInfo;

/**
 *
 * @author zinic
 */
public interface Context {

   ManagementBeanBuilder generateMonitor();
   
   ManagementBeanInfo[] contextInfo();
}
