package net.jps.sjmx.command.jmx;

import java.io.IOException;
import java.util.Set;
import javax.management.*;
import javax.management.remote.JMXConnector;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.config.ConfigurationManager;

/**
 *
 * @author zinic
 */
public class Describe extends AbstractJmxCommand {

    public Describe(ConfigurationManager configurationManager) {
        super(configurationManager);
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
        try {
            final JMXConnector jmxConnector = connect();
            final CommandResult result = describeMBean(mbeanName, jmxConnector.getMBeanServerConnection());

            jmxConnector.close();

            return result;
        } catch (Exception ex) {
            return new ExceptionResult(ex);
        }
    }

    private CommandResult describeMBean(String mbeanName, MBeanServerConnection mBeanServerConnection) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        try {
            final Set<ObjectName> foundObjectNames = mBeanServerConnection.queryNames(ObjectName.getInstance(mbeanName), null);
            
            if (!foundObjectNames.isEmpty()) {
                final ObjectName first = foundObjectNames.iterator().next();
                final MBeanInfo mbeanInfo = mBeanServerConnection.getMBeanInfo(first);
                
                for (MBeanAttributeInfo mBeanAttributeInfo : mbeanInfo.getAttributes()) {
                    if (mBeanAttributeInfo.isReadable()) {
                        stringBuilder.append("Attribute: ").append(mBeanAttributeInfo.getName()).append("\n");
                    }
                }
            } else {
                return new MessageResult("Unable to locate any MBean bound to full-name or query string: " + mbeanName);
            }
        } catch (Exception exception) {
            return new ExceptionResult(exception);
        }

        return new MessageResult(stringBuilder.toString());
    }
}
