package net.jps.sjmx.command.jmx;

import net.jps.sjmx.cli.command.AbstractCommandList;
import net.jps.sjmx.command.jmx.domain.DomainCommandList;
import net.jps.sjmx.command.jmx.mbean.MBeanCommandList;
import net.jps.sjmx.config.ConfigurationManager;

/**
 *
 * @author zinic
 */
public class JMXCommandList extends AbstractCommandList {

    public JMXCommandList(ConfigurationManager configurationManager) {
        super(new DomainCommandList(configurationManager), new MBeanCommandList(configurationManager), new Describe(configurationManager));
    }

    @Override
    public String getCommandToken() {
        return "jmx";
    }

    @Override
    public String getCommandDescription() {
        return "JMX commands.";
    }
}
