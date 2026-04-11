package bot.inker.kook4j.exception;

public class KookNotFoundException extends KookApiException {

    public KookNotFoundException(int code, String message) {
        super(code, message);
    }
}
