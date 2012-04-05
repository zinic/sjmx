package jmx.model;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author zinic
 */
public class ManagementBean {

    private List<Attribute> attributes;
    private String name, type;

    public List<Attribute> getAttributes() {
        if (attributes == null) {
            attributes = new LinkedList<Attribute>();
        }
        
        return attributes;
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
