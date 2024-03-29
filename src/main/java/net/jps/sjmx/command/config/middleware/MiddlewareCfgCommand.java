package net.jps.sjmx.command.config.middleware;

import net.jps.jx.Controls;
import net.jps.sjmx.cli.command.AbstractCommandList;
import net.jps.sjmx.config.ConfigurationReader;

/**
 *
 * @author zinic
 */
public class MiddlewareCfgCommand extends AbstractCommandList {

    public MiddlewareCfgCommand(ConfigurationReader configurationManager) {
        super(new Add(configurationManager),
                new List(configurationManager),
                new DescribeMiddlewareCommand(configurationManager, Controls.getJxControls()));
    }

    @Override
    public String getCommandToken() {
        return "middleware";
    }

    @Override
    public String getCommandDescription() {
        return "Manage JMX middleware registrations.";
    }
}
