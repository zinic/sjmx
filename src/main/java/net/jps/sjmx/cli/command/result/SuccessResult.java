package net.jps.sjmx.cli.command.result;

/**
 *
 * @author zinic
 */
public class SuccessResult implements CommandResult {

    @Override
    public int getStatusCode() {
        return StatusCodes.OK.intValue();
    }

    @Override
    public String getStringResult() {
        return null;
    }
}
