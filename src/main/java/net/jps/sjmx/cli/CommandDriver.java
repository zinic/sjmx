package net.jps.sjmx.cli;

import java.util.Arrays;
import net.jps.sjmx.cli.command.Command;
import net.jps.sjmx.cli.command.result.CommandResult;

/**
 *
 * @author zinic
 */
public class CommandDriver {

    private final Command myCommand;
    private final String[] args;

    public CommandDriver(Command command, String[] args) {
        this.myCommand = command;
        this.args = Arrays.copyOf(args, args.length);
    }

    public CommandResult go() {
        return args.length > 0 ? nextCommand(args[0]) : myCommand.perform(args);
    }

    public CommandResult nextCommand(String nextArgument) {
        if (nextArgument == null && nextArgument.length() == 0) {
            throw new IllegalArgumentException();
        }

        for (Command availableCommand : myCommand.availableCommands()) {
            if (availableCommand.getCommandToken().equalsIgnoreCase(nextArgument)) {
                return new CommandDriver(availableCommand, Arrays.copyOfRange(args, 1, args.length)).go();
            }
        }

        return myCommand.perform(args);
    }
}
