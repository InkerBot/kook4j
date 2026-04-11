package bot.inker.kook4j.exception;

public class KookWebSocketException extends KookConnectionException {

    public KookWebSocketException(String message) {
        super(message);
    }

    public KookWebSocketException(String message, Throwable cause) {
        super(message, cause);
    }
}