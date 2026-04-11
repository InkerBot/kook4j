package bot.inker.kook4j.exception;

public class KookConnectionException extends KookException {

    public KookConnectionException(String message) {
        super(message);
    }

    public KookConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
