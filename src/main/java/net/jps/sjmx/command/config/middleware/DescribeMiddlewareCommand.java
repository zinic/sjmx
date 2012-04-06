package net.jps.sjmx.command.config.middleware;

import net.jps.sjmx.command.jmx.*;
import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import jmx.model.builder.AliasedAttribute;
import jmx.model.builder.ManagementBeanBuilderImpl;
import jmx.model.info.AttributeInfo;
import jmx.model.info.ManagementBeanInfo;
import jmx.model.info.ManagementBeanInfoBuilder;
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
import net.jps.sjmx.python.JythonObjectFactory;
import org.codehaus.jackson.JsonFactory;
import org.python.core.Options;
import org.python.core.Py;
import org.python.util.PythonInterpreter;
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
    public CommandResult perform(String[] arguments) throws ConfigurationException, IOException, ClassNotFoundException, JxWritingException {
        if (arguments.length != 1) {
            return new InvalidArguments("Expecting a Jython middleware filename or classname.");
        }

        final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();
        final SJMXConnector currentConnector = cfgHandler.currentConnector();
        final MiddlewarePipeline pipeline = currentConnector.getPipeline();

        if (pipeline == null) {
            return new MessageResult("No pipeline configured for remote: " + currentConnector.getId());
        }

        final Map<String, List<ManagementBeanInfo>> remoteMBeanGraph = describeMBeans();

        for (MiddlewareReference middlewareRef : pipeline.getFilter()) {
            if (arguments[0].equals(middlewareRef.getClassName()) || arguments[0].equals(middlewareRef.getHref())) {
                return processMiddleware(middlewareRef, remoteMBeanGraph);
            }
        }

        return new MessageResult("Unable to locate middleware: " + arguments[0]);
    }

    private CommandResult processMiddleware(MiddlewareReference middlewareRef, Map<String, List<ManagementBeanInfo>> remoteMBeanGraph) throws ConfigurationException, ClassNotFoundException, IOException, JxWritingException {
        final File middlewareFile = new File(middlewareRef.getHref());

        if (!middlewareFile.exists()) {
            throw new ConfigurationException("Unable to locate python JMX middleware file: " + middlewareFile.getAbsolutePath());
        }

        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        // Shut up Jython
        Options.verbose = -1;
        
        final PythonInterpreter pyInterpreter = new PythonInterpreter();
        pyInterpreter.setOut(output);
        pyInterpreter.setErr(output);
        
        final JythonObjectFactory<JMXFilterlet> filterletFactory = new JythonObjectFactory<JMXFilterlet>(pyInterpreter, JMXFilterlet.class);
        loadMiddleware(pyInterpreter, middlewareFile);

        final ManagementBeanBuilderImpl managementBeanBuilder = new ManagementBeanBuilderImpl("sjmx.management", middlewareRef.getClassName());
        final JMXFilterlet filterletInstance = filterletFactory.createObject(middlewareRef.getClassName());
        final Context ctx = new FilterletContext(managementBeanBuilder, remoteMBeanGraph);

        output.reset();
        filterletInstance.perform(ctx);

        final ManagementBeanInfo managementBeanInfo = new ManagementBeanInfo();
        managementBeanInfo.setDomain(managementBeanBuilder.getDomainName());
        managementBeanInfo.setName(managementBeanBuilder.getName());
        managementBeanInfo.setType("JythonMBeanPoxy");

        for (Map.Entry<String, AliasedAttribute> aliasEntry : managementBeanBuilder.getAttributeAliases().entrySet()) {
            final AttributeInfo attributeInfo = new AttributeInfo(aliasEntry.getValue().getAttributeInfo());
            attributeInfo.setName(aliasEntry.getKey());

            managementBeanInfo.getAttributes().add(attributeInfo);
        }

        final JsonWriter<ManagementBeanInfo> mbeanJsonWriter = new JacksonJsonWriter<ManagementBeanInfo>(new JsonFactory(), StaticFieldMapper.getInstance());
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mbeanJsonWriter.write(managementBeanInfo, baos);

        final StringBuilder outputString = new StringBuilder("Result: ");
        outputString.append(new String(baos.toByteArray())).append("\n\nOutput\n").append(new String(output.toByteArray()));
        
        return new MessageResult(new String(baos.toByteArray()));
    }

    private void loadMiddleware(PythonInterpreter pyInterpreter, File middlewareFile) throws FileNotFoundException, IOException {
        // Load the middleware
        final FileInputStream fin = new FileInputStream(middlewareFile);
        pyInterpreter.execfile(fin);

        fin.close();
    }

    private Map<String, List<ManagementBeanInfo>> describeMBeans() {
        try {
            final JMXConnector jmxConnector = connect();
            final Map<String, List<ManagementBeanInfo>> remoteMBeanGraph = describeMBeans(jmxConnector.getMBeanServerConnection());

            jmxConnector.close();

            return remoteMBeanGraph;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private Map<String, List<ManagementBeanInfo>> describeMBeans(MBeanServerConnection mBeanServerConnection) throws Exception {
        final Map<String, List<ManagementBeanInfo>> remoteMBeanGraph = new HashMap<String, List<ManagementBeanInfo>>();

        for (String domainName : mBeanServerConnection.getDomains()) {
            final List<ManagementBeanInfo> mBeanInfoList = new LinkedList<ManagementBeanInfo>();

            for (ObjectName objectName : mBeanServerConnection.queryNames(ObjectName.getInstance(domainName + ":*"), null)) {
                final MBeanInfo info = mBeanServerConnection.getMBeanInfo(objectName);

                mBeanInfoList.add(new ManagementBeanInfoBuilder(objectName, info).build());
            }

            remoteMBeanGraph.put(domainName, mBeanInfoList);
        }

        return remoteMBeanGraph;
    }
}