package net.jps.sjmx.cli.command.result;

/**
 *
 * @author zinic
 */
public class MessageResult implements CommandResult {

   public final String message;

   public MessageResult(String message) {
      this.message = message;
   }

   @Override
   public String getStringResult() {
      return message;
   }

   @Override
   public int getStatusCode() {
      return StatusCodes.OK.intValue();
   }
}
