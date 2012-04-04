package net.jps.sjmx.cli.command;

import net.jps.sjmx.cli.command.result.CommandResult;

/**
 *
 * @author zinic
 */
public interface Command {

   String getCommandToken();
   
   String getCommandDescription();
   
   CommandResult perform(String[] arguments);
   
   Command[] availableCommands();
}
