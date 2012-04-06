package jmx.model.info;

import java.util.Hashtable;

/**
 *
 * @author zinic
 */
public class ManagementBeanParameters {

    final ManagementBeanComponentInfo componentInfo;

    public ManagementBeanParameters(ManagementBeanComponentInfo componentInfo) {
        this.componentInfo = componentInfo;
    }

    public Hashtable<String, String> getSearchMap() {
        final Hashtable<String, String> searchMap = new Hashtable<String, String>();

        if (componentInfo.getName() != null) {
            searchMap.put("name", componentInfo.getName());
        }

        if (componentInfo.getType() != null) {
            searchMap.put("type", componentInfo.getType());
        }
        
        return searchMap;
    }
}
