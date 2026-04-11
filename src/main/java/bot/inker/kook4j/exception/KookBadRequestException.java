package bot.inker.kook4j.exception;

public class KookBadRequestException extends KookApiException {

    public KookBadRequestException(int code, String message) {
        super(code, message);
    }
}
