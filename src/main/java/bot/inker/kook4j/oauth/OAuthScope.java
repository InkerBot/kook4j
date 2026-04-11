package bot.inker.kook4j.oauth;

public enum OAuthScope {

    GET_USER_INFO("get_user_info"),
    GET_USER_GUILDS("get_user_guilds");

    private final String value;

    OAuthScope(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
