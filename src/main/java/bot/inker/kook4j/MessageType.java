package bot.inker.kook4j;

public enum MessageType {

    TEXT(1),
    IMAGE(2),
    VIDEO(3),
    FILE(4),
    AUDIO(8),
    KMARKDOWN(9),
    CARD(10),
    ITEM(12),
    SYSTEM(255);

    private final int value;

    MessageType(int value) {
        this.value = value;
    }

    public static MessageType of(int value) {
        return switch (value) {
            case 1 -> TEXT;
            case 2 -> IMAGE;
            case 3 -> VIDEO;
            case 4 -> FILE;
            case 8 -> AUDIO;
            case 9 -> KMARKDOWN;
            case 10 -> CARD;
            case 12 -> ITEM;
            case 255 -> SYSTEM;
            default -> throw new IllegalArgumentException("Unknown message type: " + value);
        };
    }

    public int value() {
        return value;
    }
}