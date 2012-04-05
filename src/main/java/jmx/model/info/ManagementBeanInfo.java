package jmx.model.info;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author zinic
 */
public class ManagementBeanInfo {

    private List<AttributeInfo> attributes;
    private String name, type, className, description;

    public ManagementBeanInfo() {
    }

    public List<AttributeInfo> getAttributes() {
        if (attributes == null) {
            attributes = new LinkedList<AttributeInfo>();
        }

        return attributes;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
