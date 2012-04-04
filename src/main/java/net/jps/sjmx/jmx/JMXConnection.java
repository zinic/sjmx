package net.jps.sjmx.jmx;

import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import net.jps.sjmx.config.model.JMXCredentials;
import net.jps.sjmx.config.model.SJMXConnector;

/**
 *
 * @author zinic
 */
public class JMXConnection {

    private final SJMXConnector sjmxConnector;

    public JMXConnection(SJMXConnector sjmxConnector) {
        this.sjmxConnector = sjmxConnector;
    }

    public JMXConnector connect() throws JMXConnectionException {
        final String urlString = "service:jmx:rmi:///jndi/rmi://" + sjmxConnector.getHost() + ":" + sjmxConnector.getPort().toString() + "/jmxrmi";
        final Map<String, Object> jmxEnv = new HashMap<String, Object>();

        if (sjmxConnector.getCredentials() != null) {
            final JMXCredentials credentials = sjmxConnector.getCredentials();
            jmxEnv.put(JMXConnector.CREDENTIALS, new String[]{credentials.getUsername(), credentials.getPassword()});
        }

        try {
            final JMXServiceURL serviceURL = new JMXServiceURL(urlString);
            
            return JMXConnectorFactory.connect(serviceURL, jmxEnv);
        } catch (Exception ex) {
            throw new JMXConnectionException(ex.getMessage(), ex);
        }
    }
}
