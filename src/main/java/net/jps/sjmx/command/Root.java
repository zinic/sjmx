package net.jps.sjmx.command;

import net.jps.sjmx.command.jmx.DescribeCommand;
import net.jps.sjmx.command.jmx.domain.DomainCommand;
import net.jps.sjmx.command.remote.RemoteCfgCommand;
import net.jps.sjmx.config.ConfigurationManager;

/**
 *
 * @author zinic
 */
public class Root extends net.jps.sjmx.cli.command.RootCommand {

    public Root(ConfigurationManager configurationManager) {
        super(new RemoteCfgCommand(configurationManager),
                new DomainCommand(configurationManager),
                new DescribeCommand(configurationManager));
    }
}
