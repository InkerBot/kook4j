package bot.inker.kook4j;

public enum VoiceQuality {

    LOW("1"),
    NORMAL("2"),
    HIGH("3");

    private final String value;

    VoiceQuality(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
