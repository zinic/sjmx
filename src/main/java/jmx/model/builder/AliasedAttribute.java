package jmx.model.builder;

import jmx.model.info.AttributeInfo;
import jmx.model.info.ManagementBeanInfo;

/**
 *
 * @author zinic
 */
public class AliasedAttribute {

    private final ManagementBeanInfo managementBeanInfo;
    private final AttributeInfo attributeInfo;

    public AliasedAttribute(ManagementBeanInfo managementBeanInfo, AttributeInfo attributeInfo) {
        this.managementBeanInfo = managementBeanInfo;
        this.attributeInfo = attributeInfo;
    }

    public AttributeInfo getAttributeInfo() {
        return attributeInfo;
    }

    public ManagementBeanInfo getManagementBeanInfo() {
        return managementBeanInfo;
    }
}
