package net.jps.sjmx.command.jmx.domain;

import net.jps.sjmx.cli.command.AbstractCommandList;
import net.jps.sjmx.config.ConfigurationManager;

/**
 *
 * @author zinic
 */
public class DomainCommandList extends AbstractCommandList {

    public DomainCommandList(ConfigurationManager configurationManager) {
        super(new DomainCommand(configurationManager));
    }

    @Override
    public String getCommandToken() {
        return "domains";
    }

    @Override
    public String getCommandDescription() {
        return "JMX domain specific commands.";
    }
}
