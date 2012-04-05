package net.jps.sjmx.command.jmx.domain;

import java.io.IOException;
import javax.management.*;
import javax.management.remote.JMXConnector;
import net.jps.sjmx.cli.command.result.*;
import net.jps.sjmx.command.jmx.AbstractJmxCommand;
import net.jps.sjmx.config.ConfigurationReader;

/**
 *
 * @author zinic
 */
public class DomainCommand extends AbstractJmxCommand {

    public DomainCommand(ConfigurationReader configurationManager) {
        super(configurationManager);
    }

    @Override
    public String getCommandDescription() {
        return "Lists available JMX domains and their related MBeans.";
    }

    @Override
    public String getCommandToken() {
        return "domain";
    }

    @Override
    public CommandResult perform(String[] arguments) {
        if (arguments.length == 0) {
            return listDomains();
        } else {
            return listMBeans(arguments[0]);
        }
    }

    private CommandResult listMBeans(String domain) {
        final StringBuilder stringBuilder = new StringBuilder();
        
        try {
            final JMXConnector jmxConnector = connect();
            
            for (ObjectName objectName : jmxConnector.getMBeanServerConnection().queryNames(ObjectName.getInstance(domain + ":*"), null)) {
                stringBuilder.append(objectName.toString()).append("\n");
            }
            
            jmxConnector.close();
        } catch (Exception ex) {
            return new ExceptionResult(ex);
        }

        return new MessageResult(stringBuilder.toString());
    }

    private CommandResult listDomains() {
        try {
            final JMXConnector jmxConnector = connect();
            final CommandResult result = listDomains(jmxConnector.getMBeanServerConnection());

            jmxConnector.close();

            return result;
        } catch (Exception ex) {
            return new ExceptionResult(ex);
        }
    }

    private CommandResult listDomains(MBeanServerConnection mBeanServerConnection) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder();

        for (String domain : mBeanServerConnection.getDomains()) {
            stringBuilder.append(domain).append("\n");
        }

        return new MessageResult(stringBuilder.toString());
    }
}
