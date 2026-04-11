package bot.inker.kook4j.exception;

public class KookApiException extends KookException {

    private final int code;

    public KookApiException(int code, String message) {
        super(message);
        this.code = code;
    }

    public KookApiException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int code() {
        return code;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{code=" + code + ", message='" + getMessage() + "'}";
    }
}
