package bot.inker.kook4j;

public enum MessageListDirection {

    BEFORE("before"),
    AROUND("around"),
    AFTER("after");

    private final String value;

    MessageListDirection(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
