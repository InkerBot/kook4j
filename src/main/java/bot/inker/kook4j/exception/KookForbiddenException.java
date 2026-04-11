package bot.inker.kook4j.exception;

public class KookForbiddenException extends KookApiException {

    public KookForbiddenException(int code, String message) {
        super(code, message);
    }
}
