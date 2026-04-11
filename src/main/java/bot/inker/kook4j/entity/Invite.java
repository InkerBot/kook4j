package bot.inker.kook4j.entity;

public final class Invite {

    private String channelId;
    private String guildId;
    private String urlCode;
    private String url;
    private User user;
    private int duration;
    private int usingTimes;
    private int remainingTimes;

    Invite() {
    }

    private Invite(Builder builder) {
        this.channelId = builder.channelId;
        this.guildId = builder.guildId;
        this.urlCode = builder.urlCode;
        this.url = builder.url;
        this.user = builder.user;
        this.duration = builder.duration;
        this.usingTimes = builder.usingTimes;
        this.remainingTimes = builder.remainingTimes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String channelId() {
        return channelId;
    }

    public String guildId() {
        return guildId;
    }

    public String urlCode() {
        return urlCode;
    }

    public String url() {
        return url;
    }

    public User user() {
        return user;
    }

    public int duration() {
        return duration;
    }

    public int usingTimes() {
        return usingTimes;
    }

    public int remainingTimes() {
        return remainingTimes;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "Invite{urlCode='" + urlCode + "', guildId='" + guildId + "'}";
    }

    public static final class Builder {
        private String channelId;
        private String guildId;
        private String urlCode;
        private String url;
        private User user;
        private int duration;
        private int usingTimes;
        private int remainingTimes;

        Builder() {
        }

        Builder(Invite src) {
            this.channelId = src.channelId;
            this.guildId = src.guildId;
            this.urlCode = src.urlCode;
            this.url = src.url;
            this.user = src.user;
            this.duration = src.duration;
            this.usingTimes = src.usingTimes;
            this.remainingTimes = src.remainingTimes;
        }

        public Builder channelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder guildId(String guildId) {
            this.guildId = guildId;
            return this;
        }

        public Builder urlCode(String urlCode) {
            this.urlCode = urlCode;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder usingTimes(int usingTimes) {
            this.usingTimes = usingTimes;
            return this;
        }

        public Builder remainingTimes(int remainingTimes) {
            this.remainingTimes = remainingTimes;
            return this;
        }

        public Invite build() {
            return new Invite(this);
        }
    }
}
