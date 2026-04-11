package bot.inker.kook4j.exception;

public class KookRateLimitException extends KookApiException {

    private final int limit;
    private final int remaining;
    private final int resetSeconds;
    private final String bucket;
    private final boolean global;

    public KookRateLimitException(int code, String message,
                                  int limit, int remaining, int resetSeconds,
                                  String bucket, boolean global) {
        super(code, message);
        this.limit = limit;
        this.remaining = remaining;
        this.resetSeconds = resetSeconds;
        this.bucket = bucket;
        this.global = global;
    }

    public int limit() {
        return limit;
    }

    public int remaining() {
        return remaining;
    }

    public int resetSeconds() {
        return resetSeconds;
    }

    public String bucket() {
        return bucket;
    }

    public boolean global() {
        return global;
    }
}
