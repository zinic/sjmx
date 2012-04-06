package jmx.model.info;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author zinic
 */
public class ManagementBeanInfo extends ManagementBeanComponentInfo {

    private List<AttributeInfo> attributes;
    private List<OperationInfo> operations;
    private String domain, className;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<OperationInfo> getOperations() {
        if (operations == null) {
            operations = new LinkedList<OperationInfo>();
        }

        return operations;
    }

    public List<AttributeInfo> getAttributes() {
        if (attributes == null) {
            attributes = new LinkedList<AttributeInfo>();
        }

        return attributes;
    }
}
