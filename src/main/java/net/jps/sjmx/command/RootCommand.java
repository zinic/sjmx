package net.jps.sjmx.command;

import net.jps.sjmx.command.jmx.Describe;
import net.jps.sjmx.command.jmx.domain.DomainCommand;
import net.jps.sjmx.command.jmx.mbean.MBeanCommandList;
import net.jps.sjmx.command.remote.RemoteCommandList;
import net.jps.sjmx.config.ConfigurationManager;

/**
 *
 * @author zinic
 */
public class RootCommand extends net.jps.sjmx.cli.command.RootCommand {

    public RootCommand(ConfigurationManager configurationManager) {
        super(new RemoteCommandList(configurationManager),
                new DomainCommand(configurationManager),
                new MBeanCommandList(configurationManager),
                new Describe(configurationManager));
    }
}
