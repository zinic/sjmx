package net.jps.sjmx.command.jmx;

import jmx.model.proxy.ProxyManagementBeanInfoBuilder;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import javax.management.*;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import jmx.model.info.ManagementDomainInfo;
import net.jps.jx.JxWritingException;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.command.ConfigurationAwareCommand;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationHandler;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.config.model.MiddlewarePipeline;
import net.jps.sjmx.config.model.MiddlewareReference;
import net.jps.sjmx.config.model.SJMXConnector;
import net.jps.sjmx.jmx.*;
import net.jps.sjmx.plugin.InterpreterContext;
import net.jps.sjmx.plugin.ObjectFactory;
import net.jps.sjmx.plugin.python.PythonInterpreterContext;
import sjmx.filter.FilterletContext;
import sjmx.filter.JMXFilterlet;

/**
 *
 * @author zinic
 */
public class JMXMiddleware extends ConfigurationAwareCommand {

    public JMXMiddleware(ConfigurationReader configurationManager) {
        super(configurationManager);
    }

    @Override
    public String getCommandDescription() {
        return "Test runs the python JMX middleware pipeline.";
    }

    @Override
    public String getCommandToken() {
        return "jmxm";
    }

    @Override
    public CommandResult perform(String[] arguments) {
        try {
            final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            final JMXServiceURL serviceURL = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/server");
            final JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(serviceURL, Collections.EMPTY_MAP, mBeanServer);

            final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();

            for (SJMXConnector currentConnector : cfgHandler.connectorList()) {
                final MiddlewarePipeline pipeline = currentConnector.getPipeline();

                if (pipeline == null) {
                    return new MessageResult("No pipeline configured for remote: " + currentConnector.getId());
                }

                final JMXConnectorFactory connectorFactory = new JMXConnectorFactoryImpl(currentConnector);

                try {
                    final JMXConnector jMXConnector = connectorFactory.newConnector();
                    final JMXInfoGraphBuilder graphBuilder = new JMXInfoGraphBuilder(jMXConnector);

                    for (ProxyManagementBean mbeanProxy : inspectJmxRemote(graphBuilder, connectorFactory, pipeline, currentConnector)) {
                        mBeanServer.registerMBean(mbeanProxy, mbeanProxy.getObjectName());
                    }

                    jMXConnector.close();
                } catch (Exception ex) {
                    System.out.println("Failed to connecto to: " + currentConnector.getId());
                }
            }

            connectorServer.start();

            while (true) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                    break;
                }
            }

            connectorServer.stop();
        } catch (Throwable t) {
            throw new FatalException(t);
        }

        return new SuccessResult();
    }

    private List<ProxyManagementBean> inspectJmxRemote(JMXInfoGraphBuilder graphBuilder, JMXConnectorFactory connectorFactory, MiddlewarePipeline pipeline, SJMXConnector currentConnector) throws IOException, ClassNotFoundException, MBeanRegistrationException, InstanceAlreadyExistsException, NotCompliantMBeanException, ConfigurationException, JxWritingException, MalformedObjectNameException, JMXConnectionException {
        final List<ManagementDomainInfo> remoteMBeanGraph = graphBuilder.getInfoGraph();
        final List<ProxyManagementBean> proxyMbeans = new LinkedList<ProxyManagementBean>();

        for (MiddlewareReference middlewareRef : pipeline.getFilter()) {
            final ProxyManagementBeanInfoBuilder proxyBuilder = processMiddleware(currentConnector.getId(), middlewareRef, remoteMBeanGraph);

            proxyMbeans.add(proxyBuilder.newProxyManagementBean(connectorFactory));
        }

        return proxyMbeans;
    }

    private ProxyManagementBeanInfoBuilder processMiddleware(String domain, MiddlewareReference middlewareRef, List<ManagementDomainInfo> remoteMBeanGraph) throws ConfigurationException, ClassNotFoundException, IOException, JxWritingException {
        final File middlewareFile = new File(middlewareRef.getHref());

        if (!middlewareFile.exists()) {
            throw new ConfigurationException("Unable to locate JMX middleware file: " + middlewareFile.getAbsolutePath());
        }

        final InterpreterContext interpreterContext = new PythonInterpreterContext();

        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        interpreterContext.setStdOut(output);
        interpreterContext.setStdErr(output);

        final ObjectFactory<JMXFilterlet> filterletFactory = interpreterContext.newObjectFactory(JMXFilterlet.class);
        interpreterContext.load(middlewareFile);

        final ProxyManagementBeanInfoBuilder managementBeanBuilder = new ProxyManagementBeanInfoBuilder("sjmx." + domain, middlewareRef.getClassName());
        final JMXFilterlet filterletInstance = filterletFactory.createObject(middlewareRef.getClassName());

        filterletInstance.perform(new FilterletContext(managementBeanBuilder, remoteMBeanGraph));

        return managementBeanBuilder;
    }
}