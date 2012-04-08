package net.jps.sjmx.jmx;

import java.util.LinkedList;
import java.util.List;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import jmx.model.info.ManagementBeanInfoBuilder;
import jmx.model.info.ManagementDomainInfo;

/**
 *
 * @author zinic
 */
public class JMXInfoGraphBuilder {

    private final JMXConnector jMXConnection;

    public JMXInfoGraphBuilder(JMXConnector jMXConnection) {
        this.jMXConnection = jMXConnection;
    }

    public JMXConnector getjMXConnection() {
        return jMXConnection;
    }

    public List<ManagementDomainInfo> getInfoGraph() {
        try {
            return describeMBeans(jMXConnection.getMBeanServerConnection());
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private List<ManagementDomainInfo> describeMBeans(MBeanServerConnection mBeanServerConnection) throws Exception {
        final List<ManagementDomainInfo> remoteMBeanGraph = new LinkedList<ManagementDomainInfo>();

        for (String domainName : mBeanServerConnection.getDomains()) {
            final ManagementDomainInfo domainInfo = new ManagementDomainInfo();
            domainInfo.setName(domainName);

            for (ObjectName objectName : mBeanServerConnection.queryNames(ObjectName.getInstance(domainName + ":*"), null)) {
                final MBeanInfo info = mBeanServerConnection.getMBeanInfo(objectName);

                domainInfo.getManagementBeans().add(new ManagementBeanInfoBuilder(objectName, info).build());
            }

            remoteMBeanGraph.add(domainInfo);
        }

        return remoteMBeanGraph;
    }
}
