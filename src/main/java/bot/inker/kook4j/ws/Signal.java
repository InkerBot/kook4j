package bot.inker.kook4j.ws;

public enum Signal {

    EVENT(0),
    HELLO(1),
    PING(2),
    PONG(3),
    RESUME(4),
    RECONNECT(5),
    RESUME_ACK(6);

    private final int value;

    Signal(int value) {
        this.value = value;
    }

    public static Signal of(int value) {
        return switch (value) {
            case 0 -> EVENT;
            case 1 -> HELLO;
            case 2 -> PING;
            case 3 -> PONG;
            case 4 -> RESUME;
            case 5 -> RECONNECT;
            case 6 -> RESUME_ACK;
            default -> throw new IllegalArgumentException("Unknown signal: " + value);
        };
    }

    public int value() {
        return value;
    }
}