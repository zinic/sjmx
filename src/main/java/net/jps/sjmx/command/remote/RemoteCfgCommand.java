package net.jps.sjmx.command.remote;

import net.jps.sjmx.cli.command.AbstractCommandList;
import net.jps.sjmx.config.ConfigurationManager;

/**
 *
 * @author zinic
 */
public class RemoteCfgCommand extends AbstractCommandList {

    public RemoteCfgCommand(ConfigurationManager configurationManager) {
        super(new Add(configurationManager), new List(configurationManager), new Remove(configurationManager), new Use(configurationManager));
    }

    @Override
    public String getCommandToken() {
        return "remote";
    }

    @Override
    public String getCommandDescription() {
        return "manage JMX remote connections";
    }
}
