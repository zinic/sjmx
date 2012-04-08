package net.jps.sjmx.command.jmx;

import jmx.model.proxy.ProxyManagementBeanInfoBuilder;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.util.List;
import javax.management.MBeanServer;
import jmx.model.info.ManagementDomainInfo;
import net.jps.jx.JxWritingException;
import net.jps.sjmx.cli.command.result.*;
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
public class JMXMiddleware extends AbstractJmxCommand {

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
            final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();
            final SJMXConnector currentConnector = cfgHandler.currentConnector();
            final MiddlewarePipeline pipeline = currentConnector.getPipeline();

            if (pipeline == null) {
                return new MessageResult("No pipeline configured for remote: " + currentConnector.getId());
            }

            final JMXInfoGraphBuilder graphBuilder = new JMXInfoGraphBuilder(connect());

            try {
                final MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
                final List<ManagementDomainInfo> remoteMBeanGraph = graphBuilder.getInfoGraph();
                final JMXConnectorFactory connectorFactory = new JMXConnectorFactoryImpl(getConfigurationReader());

                for (MiddlewareReference middlewareRef : pipeline.getFilter()) {
                    final ProxyManagementBeanInfoBuilder proxyBuilder = processMiddleware(middlewareRef, remoteMBeanGraph);

                    mBeanServer.registerMBean(proxyBuilder.newProxyManagementBean(connectorFactory), proxyBuilder.proxyInfo().getObjectName());
                }
            } finally {
                graphBuilder.getjMXConnection().close();
            }
        } catch (Throwable t) {
            throw new FatalException(t);
        }

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
                throw new FatalException(ie);
            }
        }
    }

    private ProxyManagementBeanInfoBuilder processMiddleware(MiddlewareReference middlewareRef, List<ManagementDomainInfo> remoteMBeanGraph) throws ConfigurationException, ClassNotFoundException, IOException, JxWritingException {
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

        final ProxyManagementBeanInfoBuilder managementBeanBuilder = new ProxyManagementBeanInfoBuilder("sjmx.management", middlewareRef.getClassName());
        final JMXFilterlet filterletInstance = filterletFactory.createObject(middlewareRef.getClassName());

        filterletInstance.perform(new FilterletContext(managementBeanBuilder, remoteMBeanGraph));

        return managementBeanBuilder;
    }
}