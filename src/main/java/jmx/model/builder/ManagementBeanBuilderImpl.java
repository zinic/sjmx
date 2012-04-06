package jmx.model.builder;

import jmx.model.info.ManagementBeanInfo;
import java.util.HashMap;
import java.util.Map;
import jmx.model.info.AttributeInfo;
import net.jps.sjmx.jmx.JMXConnectorFactory;
import net.jps.sjmx.jmx.ProxyManagementBean;

/**
 *
 * @author zinic
 */
public class ManagementBeanBuilderImpl implements ManagementBeanBuilder {

    private final Map<String, AliasedAttribute> attributeAliases;
    private final ManagementBeanInfo info;
    private final String domainName, name;

    public ManagementBeanBuilderImpl(String domainName, String name) {
        this.domainName = domainName;
        this.name = name;

        attributeAliases = new HashMap<String, AliasedAttribute>();
        info = new ManagementBeanInfo();
    }

    public ProxyManagementBean newProxyManagementBean(JMXConnectorFactory connectorFactory) {
        return new ProxyManagementBean(domainName, domainName, attributeAliases, connectorFactory);
    }

    public Map<String, AliasedAttribute> getAttributeAliases() {
        return attributeAliases;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getName() {
        return name;
    }
    
    @Override
    public void setType(String type) {
        info.setType(type);
    }

    @Override
    public void setName(String name) {
        info.setName(name);
    }

    @Override
    public void setDescription(String description) {
        info.setDescription(description);
    }

    @Override
    public void alias(ManagementBeanInfo monitorInfo, String attributeName, String alias) {
        final AttributeInfo attributeInfo = findAttribute(monitorInfo, attributeName);

        if (attributeInfo != null) {
            attributeAliases.put(alias, new AliasedAttribute(monitorInfo, attributeInfo));
        } else {
            //TODO: log this
        }
    }

    private AttributeInfo findAttribute(ManagementBeanInfo managementBeanInfo, String st) {
        for (AttributeInfo attributeInfo : managementBeanInfo.getAttributes()) {
            if (st.equals(attributeInfo.getName())) {
                return attributeInfo;
            }
        }

        return null;
    }
}
