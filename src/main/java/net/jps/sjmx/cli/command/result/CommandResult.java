package net.jps.sjmx.cli.command.result;

/**
 *
 * @author zinic
 */
public interface CommandResult {

   int getStatusCode();
   
   String getStringResult();
}
