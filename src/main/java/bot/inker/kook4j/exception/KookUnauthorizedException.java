package bot.inker.kook4j.exception;

public class KookUnauthorizedException extends KookApiException {

    public KookUnauthorizedException(int code, String message) {
        super(code, message);
    }
}
