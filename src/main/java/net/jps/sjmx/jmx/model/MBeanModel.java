package net.jps.sjmx.jmx.model;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author zinic
 */
public class MBeanModel {

    private List<MBeanAttributeInfoModel> attributes;
    private String name, type, className, description;

    public MBeanModel() {
    }

    public List<MBeanAttributeInfoModel> getAttributes() {
        if (attributes == null) {
            attributes = new LinkedList<MBeanAttributeInfoModel>();
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
