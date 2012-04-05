package net.jps.sjmx.command;

import net.jps.sjmx.cli.command.AbstractCommand;
import net.jps.sjmx.config.ConfigurationReader;

/**
 *
 * @author zinic
 */
public abstract class ConfigurationAwareCommand extends AbstractCommand {

    private ConfigurationReader configurationReader;

    public ConfigurationAwareCommand(ConfigurationReader configurationReader) {
        this.configurationReader = configurationReader;
    }

    protected final ConfigurationReader getConfigurationReader() {
        return configurationReader;
    }
}
