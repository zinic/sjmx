package jmx.model.info;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author zinic
 */
public class ManagementDomainInfo {

    private List<ManagementBeanInfo> managementBeans;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String domainName) {
        name = domainName;
    }

    public List<ManagementBeanInfo> getManagementBeans() {
        if (managementBeans == null) {
            managementBeans = new LinkedList<ManagementBeanInfo>();
        }
        
        return managementBeans;
    }
}
