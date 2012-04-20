package jmx.model.proxy;

import jmx.model.info.ManagementBeanInfo;
import java.util.HashMap;
import java.util.Map;
import jmx.model.info.AttributeInfo;
import net.jps.sjmx.jmx.JMXConnectorFactory;
import net.jps.sjmx.jmx.ProxyManagementBean;

/**
 * dat name so huge
 *
 * @author zinic
 */
public class ProxyManagementBeanInfoBuilder implements ProxyManagementBeanBuilder {

    private final Map<String, AliasedAttribute> attributeAliases;
    private final ManagementBeanInfo info;
    private final String domainName, name;

    public ProxyManagementBeanInfoBuilder(String domainName, String name) {
        this.domainName = domainName;
        this.name = name;

        attributeAliases = new HashMap<String, AliasedAttribute>();
        info = new ManagementBeanInfo();
    }

    public ProxyManagementBean newProxyManagementBean(JMXConnectorFactory connectorFactory) {
        return new ProxyManagementBean(proxyInfo(), attributeAliases, connectorFactory);
    }

    public ManagementBeanInfo proxyInfo() {
        final ManagementBeanInfo managementBeanInfo = new ManagementBeanInfo();
        managementBeanInfo.setDomain(domainName);
        managementBeanInfo.setName(name);
        managementBeanInfo.setType("SJMXMBeanPoxy");

        for (Map.Entry<String, AliasedAttribute> aliasEntry : attributeAliases.entrySet()) {
            final AttributeInfo attributeInfo = new AttributeInfo(aliasEntry.getValue().getAttributeInfo());
            attributeInfo.setName(aliasEntry.getKey());

            managementBeanInfo.getAttributes().add(attributeInfo);
        }

        return managementBeanInfo;
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
