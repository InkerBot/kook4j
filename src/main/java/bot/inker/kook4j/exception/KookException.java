package bot.inker.kook4j.exception;

public class KookException extends RuntimeException {

    public KookException(String message) {
        super(message);
    }

    public KookException(String message, Throwable cause) {
        super(message, cause);
    }
}
