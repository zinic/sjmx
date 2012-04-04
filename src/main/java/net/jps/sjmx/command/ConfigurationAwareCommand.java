package net.jps.sjmx.command;

import net.jps.sjmx.cli.command.AbstractCommand;
import net.jps.sjmx.config.ConfigurationManager;

/**
 *
 * @author zinic
 */
public abstract class ConfigurationAwareCommand extends AbstractCommand {

    private ConfigurationManager configurationManager;

    public ConfigurationAwareCommand(ConfigurationManager configurationManager) {
        this.configurationManager = configurationManager;
    }

    protected final ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }
}
