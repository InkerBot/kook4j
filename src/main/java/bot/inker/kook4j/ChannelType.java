package bot.inker.kook4j;

public enum ChannelType {

    TEXT(1),
    VOICE(2);

    private final int value;

    ChannelType(int value) {
        this.value = value;
    }

    public static ChannelType of(int value) {
        return switch (value) {
            case 1 -> TEXT;
            case 2 -> VOICE;
            default -> throw new IllegalArgumentException("Unknown channel type: " + value);
        };
    }

    public int value() {
        return value;
    }
}
