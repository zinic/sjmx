package net.jps.sjmx.command.jmx.mbean;

import java.io.IOException;
import javax.management.*;
import javax.management.remote.JMXConnector;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.command.jmx.AbstractJmxCommand;
import net.jps.sjmx.config.ConfigurationManager;

/**
 *
 * @author zinic
 */
public class ListMBeans extends AbstractJmxCommand {

    public ListMBeans(ConfigurationManager configurationManager) {
        super(configurationManager);
    }

    @Override
    public String getCommandDescription() {
        return "Lists available JMX MBeans for the given domain.";
    }

    @Override
    public String getCommandToken() {
        return "list";
    }

    @Override
    public CommandResult perform(String[] arguments) {
        if (arguments.length != 1) {
            return new InvalidArguments("Expecting JMX domain name to list MBeans from.");
        }

        return listDomainObjects(arguments[0]);
    }

    private CommandResult listDomainObjects(String domain) {
        try {
            final JMXConnector jmxConnector = connect();
            final CommandResult result = listDomainObjects(domain, jmxConnector.getMBeanServerConnection());

            jmxConnector.close();

            return result;
        } catch (Exception ex) {
            return new ExceptionResult(ex);
        }
    }

    private CommandResult listDomainObjects(String domain, MBeanServerConnection mBeanServerConnection) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        try {
            for (ObjectName objectName : mBeanServerConnection.queryNames(ObjectName.getInstance(domain + ":*"), null)) {
                stringBuilder.append(objectName.toString()).append("\n");
            }
        } catch (MalformedObjectNameException exception) {
            return new ExceptionResult(exception);
        }

        return new MessageResult(stringBuilder.toString());
    }
}
