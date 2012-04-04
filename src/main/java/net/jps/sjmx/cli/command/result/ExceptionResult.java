package net.jps.sjmx.cli.command.result;

/**
 *
 * @author zinic
 */
public class ExceptionResult implements CommandResult {

    private final Throwable throwable;

    public ExceptionResult(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public int getStatusCode() {
        return -22;
    }

    @Override
    public String getStringResult() {
        return throwable.getMessage();
    }
}
