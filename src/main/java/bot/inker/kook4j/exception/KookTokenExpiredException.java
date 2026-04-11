package bot.inker.kook4j.exception;

public class KookTokenExpiredException extends KookUnauthorizedException {

    public KookTokenExpiredException(int code, String message) {
        super(code, message);
    }
}