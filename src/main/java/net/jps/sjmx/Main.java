package net.jps.sjmx;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import net.jps.sjmx.cli.CommandDriver;
import net.jps.sjmx.cli.command.Command;
import net.jps.sjmx.cli.command.result.CommandResult;
import net.jps.sjmx.command.RootCommand;
import net.jps.sjmx.config.ConfigurationManager;
import net.jps.sjmx.config.FileConfigurationManager;
import net.jps.sjmx.config.model.Configuration;
import net.jps.sjmx.config.model.ObjectFactory;

public class Main {

    public static void main(String[] args) {
        try {
            final JAXBContext jaxbc = JAXBContext.newInstance(Configuration.class);
            final ConfigurationManager configurationManager = new FileConfigurationManager(jaxbc.createMarshaller(), jaxbc.createUnmarshaller(), new ObjectFactory());
            final Command rootCommand = new RootCommand(configurationManager);

            final CommandResult result = new CommandDriver(rootCommand, args).go();
            final String stringResult = result.getStringResult();

            if (stringResult != null && stringResult.length() > 0) {
                System.out.println(result.getStringResult());
            }

            System.exit(result.getStatusCode());
        } catch (JAXBException jaxbe) {
            System.out.println(jaxbe.getMessage());
            System.exit(1000);
        }
    }
}
