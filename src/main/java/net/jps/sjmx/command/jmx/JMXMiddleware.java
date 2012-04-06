package net.jps.sjmx.command.jmx;

import jmx.model.builder.ManagementBeanBuilder;
import jmx.model.builder.ManagementBeanBuilderImpl;
import java.io.*;
import java.util.LinkedList;
import java.util.List;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import jmx.model.info.ManagementBeanInfo;
import jmx.model.info.ManagementBeanInfoBuilder;
import net.jps.sjmx.cli.command.result.CommandResult;
import net.jps.sjmx.cli.command.result.MessageResult;
import net.jps.sjmx.cli.command.result.SuccessResult;
import net.jps.sjmx.config.ConfigurationException;
import net.jps.sjmx.config.ConfigurationHandler;
import net.jps.sjmx.config.ConfigurationReader;
import net.jps.sjmx.config.model.MiddlewarePipeline;
import net.jps.sjmx.config.model.MiddlewareReference;
import net.jps.sjmx.config.model.SJMXConnector;
import net.jps.sjmx.python.JythonObjectFactory;
import org.python.util.PythonInterpreter;
import sjmx.filter.Context;
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
    public CommandResult perform(String[] arguments) throws ConfigurationException, IOException, ClassNotFoundException {
        final PythonInterpreter pyInterpreter = new PythonInterpreter();
        final JythonObjectFactory<JMXFilterlet> filterletFactory = new JythonObjectFactory<JMXFilterlet>(pyInterpreter, JMXFilterlet.class);

        final ConfigurationHandler cfgHandler = getConfigurationReader().readConfiguration();
        final SJMXConnector currentConnector = cfgHandler.currentConnector();
        final MiddlewarePipeline pipeline = currentConnector.getPipeline();

        if (pipeline == null) {
            return new MessageResult("No pipeline configured for remote: " + currentConnector.getId());
        }

        final ManagementBeanInfo[] mbeans = describeMBeans();

        for (final MiddlewareReference middlewareRef : pipeline.getFilter()) {
            final File middlewareFile = new File(middlewareRef.getHref());

            if (!middlewareFile.exists()) {
                throw new ConfigurationException("Unable to locate python JMX middleware file: " + middlewareFile.getAbsolutePath());
            }

            loadMiddleware(pyInterpreter, middlewareFile);

            // Holy crap python!?!
            final JMXFilterlet filterletInstance = filterletFactory.createObject(middlewareRef.getClassName());
//            final Context ctx = new FilterletContext(new ManagementBeanBuilderImpl("sjmx.management", middlewareRef.getClassName()), mbeans);

//            filterletInstance.perform(ctx);
        }

        return new SuccessResult();
    }

    private void loadMiddleware(PythonInterpreter pyInterpreter, File middlewareFile) throws FileNotFoundException, IOException {
        // Load the middleware
        final FileInputStream fin = new FileInputStream(middlewareFile);
        pyInterpreter.execfile(fin);

        fin.close();
    }

    private ManagementBeanInfo[] describeMBeans() {
        try {
            final JMXConnector jmxConnector = connect();
            final ManagementBeanInfo[] result = describeMBeans(jmxConnector.getMBeanServerConnection());

            jmxConnector.close();

            return result;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    private ManagementBeanInfo[] describeMBeans(MBeanServerConnection mBeanServerConnection) throws Exception {
        final List<ManagementBeanInfo> monitorInfoList = new LinkedList<ManagementBeanInfo>();

        for (String domainName : mBeanServerConnection.getDomains()) {
            for (ObjectName objectName : mBeanServerConnection.queryNames(ObjectName.getInstance(domainName + ":*"), null)) {
                final MBeanInfo info = mBeanServerConnection.getMBeanInfo(objectName);

                monitorInfoList.add(new ManagementBeanInfoBuilder(objectName, info).build());
            }
        }

        return monitorInfoList.toArray(new ManagementBeanInfo[monitorInfoList.size()]);
    }
}