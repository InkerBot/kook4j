package bot.inker.kook4j.exception;

public class KookSessionExpiredException extends KookApiException {

    public KookSessionExpiredException(int code, String message) {
        super(code, message);
    }
}