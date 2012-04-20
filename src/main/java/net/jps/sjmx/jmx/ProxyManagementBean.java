package net.jps.sjmx.jmx;

import java.util.Map;
import javax.management.*;
import javax.management.remote.JMXConnector;
import jmx.model.proxy.AliasedAttribute;
import jmx.model.info.AttributeInfo;
import jmx.model.info.builder.MBeanInfoBuilder;
import jmx.model.info.ManagementBeanInfo;
import jmx.model.info.ManagementBeanParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author zinic
 */
public class ProxyManagementBean implements DynamicMBean {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyManagementBean.class);
    private final Map<String, AliasedAttribute> attributeAliases;
    private final JMXConnectorFactory connectorFactory;
    private final ManagementBeanInfo proxyInfo;

    public ProxyManagementBean(ManagementBeanInfo proxyInfo, Map<String, AliasedAttribute> attributeAliases, JMXConnectorFactory connectorFactory) {
        this.attributeAliases = attributeAliases;
        this.connectorFactory = connectorFactory;
        this.proxyInfo = proxyInfo;
    }

    public ObjectName getObjectName() throws MalformedObjectNameException {
        return proxyInfo.getObjectName();
    }

    private Object readAttribute(MBeanServerConnection mBeanServerConnection, AliasedAttribute attribute) throws JMXConnectionException {
        try {
            final ObjectName objectName = ObjectName.getInstance(attribute.getManagementBeanInfo().getDomain(),
                    new ManagementBeanParameters(attribute.getManagementBeanInfo()).getSearchMap());

            return mBeanServerConnection.getAttribute(objectName, attribute.getAttributeInfo().getName());
        } catch (Exception ex) {
            throw new JMXConnectionException(ex.getMessage(), ex);
        }
    }

    @Override
    public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        try {
            final JMXConnector jMXConnector = connectorFactory.newConnector();
            final AliasedAttribute aliasedAttribute = attributeAliases.get(attribute);

            if (aliasedAttribute != null) {
                return readAttribute(jMXConnector.getMBeanServerConnection(), aliasedAttribute);
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return null;
    }

    @Override
    public AttributeList getAttributes(String[] attributes) {
        final AttributeList attributeList = new AttributeList();

        try {
            final JMXConnector jMXConnector = connectorFactory.newConnector();

            for (Map.Entry<String, AliasedAttribute> aliasEntry : attributeAliases.entrySet()) {
                attributeList.add(new Attribute(
                        aliasEntry.getKey(), readAttribute(jMXConnector.getMBeanServerConnection(), aliasEntry.getValue())));
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return attributeList;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
//        final ManagementBeanInfo managementBeanInfo = new ManagementBeanInfo();
//        managementBeanInfo.setDomain(proxyInfo.getDomain());
//        managementBeanInfo.setName(proxyInfo.getName());
//        managementBeanInfo.setType("JythonMBeanPoxy");
//
//        for (Map.Entry<String, AliasedAttribute> aliasEntry : attributeAliases.entrySet()) {
//            final AttributeInfo attributeInfo = new AttributeInfo(aliasEntry.getValue().getAttributeInfo());
//            attributeInfo.setName(aliasEntry.getKey());
//
//            managementBeanInfo.getAttributes().add(attributeInfo);
//        }

        return new MBeanInfoBuilder(proxyInfo).toMBeanInfo("JythonMBeanPoxy", "JythonMBeanPoxy");
    }

    @Override
    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
