package jmx.model.proxy;

import jmx.model.info.ManagementBeanInfo;

/**
 *
 * @author zinic
 */
public interface ProxyManagementBeanBuilder {

    void alias(ManagementBeanInfo monitorInfo, String attributeName, String alias);

    void setDescription(String description);

    void setName(String name);

    void setType(String type);
}
