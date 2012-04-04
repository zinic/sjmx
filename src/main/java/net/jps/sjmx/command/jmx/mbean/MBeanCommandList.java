package net.jps.sjmx.command.jmx.mbean;

import net.jps.sjmx.cli.command.AbstractCommandList;
import net.jps.sjmx.config.ConfigurationManager;

/**
 *
 * @author zinic
 */
public class MBeanCommandList extends AbstractCommandList {

    public MBeanCommandList(ConfigurationManager configurationManager) {
        super(new ListMBeans(configurationManager));
    }

    @Override
    public String getCommandToken() {
        return "mbeans";
    }

    @Override
    public String getCommandDescription() {
        return "MBean specific JMX commands.";
    }
}