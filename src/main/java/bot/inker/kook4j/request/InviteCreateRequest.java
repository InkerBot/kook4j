package bot.inker.kook4j.request;

public final class InviteCreateRequest {

    private final String guildId;
    private final String channelId;
    private final Integer duration;
    private final Integer settingTimes;

    private InviteCreateRequest(Builder builder) {
        this.guildId = builder.guildId;
        this.channelId = builder.channelId;
        this.duration = builder.duration;
        this.settingTimes = builder.settingTimes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String guildId() {
        return guildId;
    }

    public String channelId() {
        return channelId;
    }

    public Integer duration() {
        return duration;
    }

    public Integer settingTimes() {
        return settingTimes;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private String guildId;
        private String channelId;
        private Integer duration;
        private Integer settingTimes;

        Builder() {
        }

        Builder(InviteCreateRequest src) {
            this.guildId = src.guildId;
            this.channelId = src.channelId;
            this.duration = src.duration;
            this.settingTimes = src.settingTimes;
        }

        public Builder guildId(String guildId) {
            this.guildId = guildId;
            return this;
        }

        public Builder channelId(String channelId) {
            this.channelId = channelId;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder settingTimes(int settingTimes) {
            this.settingTimes = settingTimes;
            return this;
        }

        public InviteCreateRequest build() {
            return new InviteCreateRequest(this);
        }
    }
}
