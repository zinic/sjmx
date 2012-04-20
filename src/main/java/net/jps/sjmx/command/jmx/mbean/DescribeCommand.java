package net.jps.sjmx.command.jmx.mbean;

import java.io.ByteArrayOutputStream;
import java.util.Set;
import javax.management.*;
import javax.management.remote.JMXConnector;
import net.jps.jx.JsonWriter;
import net.jps.jx.jackson.JacksonJsonWriter;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.config.ConfigurationReader;
import jmx.model.info.ManagementBeanInfo;
import jmx.model.info.builder.ManagementBeanInfoBuilder;
import net.jps.jx.JxControls;
import net.jps.sjmx.command.jmx.AbstractJmxCommand;
import org.codehaus.jackson.JsonFactory;

/**
 *
 * @author zinic
 */
public class DescribeCommand extends AbstractJmxCommand {

    private final JxControls jxControls;

    public DescribeCommand(ConfigurationReader configurationManager, JxControls jxControls) {
        super(configurationManager);

        this.jxControls = jxControls;
    }

    @Override
    public String getCommandDescription() {
        return "Describes readable attributes that are available on the requested MBean.";
    }

    @Override
    public String getCommandToken() {
        return "describe";
    }

    @Override
    public CommandResult perform(String[] arguments) {
        if (arguments.length != 1) {
            return new InvalidArguments("Expecting MBean full-name or MBean search string.");
        }

        return describeMBean(arguments[0]);
    }

    private CommandResult describeMBean(String mbeanName) {
        final MessageResult messageResult = new MessageResult();
        final JsonWriter<ManagementBeanInfo> mbeanJsonWriter = new JacksonJsonWriter<ManagementBeanInfo>(new JsonFactory(), jxControls);

        try {
            final JMXConnector jmxConnector = currentJmxRemote().newConnector();
            final MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
            final Set<ObjectName> foundObjectNames = mBeanServerConnection.queryNames(ObjectName.getInstance(mbeanName), null);

            if (!foundObjectNames.isEmpty()) {
                final ObjectName first = foundObjectNames.iterator().next();
                final MBeanInfo mbeanInfo = mBeanServerConnection.getMBeanInfo(first);

                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                mbeanJsonWriter.write(new ManagementBeanInfoBuilder(first, mbeanInfo).build(), baos);

                messageResult.append(new String(baos.toByteArray()));
            } else {
                messageResult.append("Unable to locate any MBean bound to full-name or query string: ");
                messageResult.append(mbeanName);
            }

            jmxConnector.close();
        } catch (Exception ex) {
            throw new FatalException(ex);
        }

        return messageResult;
    }
}
