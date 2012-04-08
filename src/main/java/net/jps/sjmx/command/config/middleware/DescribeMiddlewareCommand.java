package net.jps.sjmx.command.config.middleware;

import net.jps.sjmx.command.jmx.*;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import jmx.model.proxy.AliasedAttribute;
import jmx.model.proxy.ProxyManagementBeanInfoBuilder;
import jmx.model.info.AttributeInfo;
import jmx.model.info.ManagementBeanInfo;
import jmx.model.info.ManagementBeanInfoBuilder;
import jmx.model.info.ManagementDomainInfo;
import net.jps.jx.JsonWriter;
import net.jps.jx.JxWritingException;
import net.jps.jx.jackson.JacksonJsonWriter;
import net.jps.jx.mapping.reflection.StaticFieldMapper;
import net.jps.sjmx.cli.command.result.CommandResult;
import net.jps.sjmx.cli.command.result.InvalidArguments;
import net.jps.sjmx.cli.command.result.MessageResult;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationHandler;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.config.model.MiddlewarePipeline;
import net.jps.sjmx.config.model.MiddlewareReference;
import net.jps.sjmx.config.model.SJMXConnector;
import net.jps.sjmx.jmx.JMXConnectionException;
import net.jps.sjmx.jmx.JMXInfoGraphBuilder;
import net.jps.sjmx.plugin.InterpreterContext;
import net.jps.sjmx.plugin.ObjectFactory;
import net.jps.sjmx.plugin.python.PythonInterpreterContext;
import org.codehaus.jackson.JsonFactory;
import sjmx.filter.Context;
import sjmx.filter.FilterletContext;
import sjmx.filter.JMXFilterlet;

/**
 *
 * @author zinic
 */
public class DescribeMiddlewareCommand extends AbstractJmxCommand {

    public DescribeMiddlewareCommand(ConfigurationReader configurationManager) {
        super(configurationManager);
    }

    @Override
    public String getCommandDescription() {
        return "Describes a middleware script.";
    }

    @Override
    public String getCommandToken() {
        return "describe";
    }

    @Override
    public CommandResult perform(String[] arguments) throws ConfigurationException, IOException, JMXConnectionException, ClassNotFoundException, JxWritingException {
        if (arguments.length != 1) {
            return new InvalidArguments("Expecting a middleware filename or classname.");
        }

        final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();
        final SJMXConnector currentConnector = cfgHandler.currentConnector();
        final MiddlewarePipeline pipeline = currentConnector.getPipeline();

        if (pipeline == null) {
            return new MessageResult("No pipeline configured for remote: " + currentConnector.getId());
        }

        final JMXInfoGraphBuilder graphBuilder = new JMXInfoGraphBuilder(connect());

        try {
            final List<ManagementDomainInfo> remoteMBeanGraph = graphBuilder.getInfoGraph();

            for (MiddlewareReference middlewareRef : pipeline.getFilter()) {
                if (arguments[0].equals(middlewareRef.getClassName()) || arguments[0].equals(middlewareRef.getHref())) {
                    return processMiddleware(middlewareRef, remoteMBeanGraph);
                }
            }
        } finally {
            graphBuilder.getjMXConnection().close();
        }

        return new MessageResult("Unable to locate middleware: " + arguments[0]);
    }

    private CommandResult processMiddleware(MiddlewareReference middlewareRef, List<ManagementDomainInfo> remoteMBeanGraph) throws ConfigurationException, ClassNotFoundException, IOException, JxWritingException {
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
        final Context ctx = new FilterletContext(managementBeanBuilder, remoteMBeanGraph);

        // Call into the script
        filterletInstance.perform(ctx);

        return writeResult(managementBeanBuilder.proxyInfo(), output);
    }

    private MessageResult writeResult(ManagementBeanInfo managementBeanInfo, ByteArrayOutputStream output) throws IOException, JxWritingException {
        final JsonWriter<ManagementBeanInfo> mbeanJsonWriter = new JacksonJsonWriter<ManagementBeanInfo>(new JsonFactory(), StaticFieldMapper.getInstance());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mbeanJsonWriter.write(managementBeanInfo, baos);

        final MessageResult messageResult = new MessageResult();

        messageResult.append("Result\n");
        messageResult.append(new String(baos.toByteArray()));
        messageResult.append("\n\nOutput\n");
        messageResult.append(new String(output.toByteArray()));

        return messageResult;
    }
}