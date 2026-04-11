package bot.inker.kook4j;

public enum MuteType {

    /**
     * Microphone muted.
     */
    MIC(1),
    /**
     * Headset/speaker deafened.
     */
    HEADPHONE(2);

    private final int value;

    MuteType(int value) {
        this.value = value;
    }

    public static MuteType of(int value) {
        return switch (value) {
            case 1 -> MIC;
            case 2 -> HEADPHONE;
            default -> throw new IllegalArgumentException("Unknown mute type: " + value);
        };
    }

    public int value() {
        return value;
    }
}
