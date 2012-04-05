package net.jps.sjmx.command;

import net.jps.sjmx.command.config.middleware.MiddlewareCfgCommand;
import net.jps.sjmx.command.jmx.DescribeCommand;
import net.jps.sjmx.command.jmx.ReadCommand;
import net.jps.sjmx.command.jmx.domain.DomainCommand;
import net.jps.sjmx.command.config.remote.RemoteCfgCommand;
import net.jps.sjmx.command.jmx.JMXMiddleware;
import net.jps.sjmx.config.ConfigurationReader;

/**
 *
 * @author zinic
 */
public class Root extends net.jps.sjmx.cli.command.RootCommand {

    public Root(ConfigurationReader configurationManager) {
        super(new RemoteCfgCommand(configurationManager),
                new DomainCommand(configurationManager),
                new DescribeCommand(configurationManager),
                new ReadCommand(configurationManager),
                new MiddlewareCfgCommand(configurationManager),
                new JMXMiddleware(configurationManager));
    }
}
