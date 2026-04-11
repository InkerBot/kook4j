package bot.inker.kook4j.request;

import bot.inker.kook4j.VoiceQuality;

public final class ChannelUpdateRequest {

    private final String name;
    private final Integer level;
    private final String parentId;
    private final String topic;
    private final Integer slowMode;
    private final Integer limitAmount;
    private final String voiceQuality;
    private final String password;

    private ChannelUpdateRequest(Builder builder) {
        this.name = builder.name;
        this.level = builder.level;
        this.parentId = builder.parentId;
        this.topic = builder.topic;
        this.slowMode = builder.slowMode;
        this.limitAmount = builder.limitAmount;
        this.voiceQuality = builder.voiceQuality;
        this.password = builder.password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String name() {
        return name;
    }

    public Integer level() {
        return level;
    }

    public String parentId() {
        return parentId;
    }

    public String topic() {
        return topic;
    }

    public Integer slowMode() {
        return slowMode;
    }

    public Integer limitAmount() {
        return limitAmount;
    }

    public String voiceQuality() {
        return voiceQuality;
    }

    public String password() {
        return password;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private String name;
        private Integer level;
        private String parentId;
        private String topic;
        private Integer slowMode;
        private Integer limitAmount;
        private String voiceQuality;
        private String password;

        Builder() {
        }

        Builder(ChannelUpdateRequest src) {
            this.name = src.name;
            this.level = src.level;
            this.parentId = src.parentId;
            this.topic = src.topic;
            this.slowMode = src.slowMode;
            this.limitAmount = src.limitAmount;
            this.voiceQuality = src.voiceQuality;
            this.password = src.password;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder slowMode(int slowMode) {
            this.slowMode = slowMode;
            return this;
        }

        public Builder limitAmount(int limitAmount) {
            this.limitAmount = limitAmount;
            return this;
        }

        public Builder voiceQuality(VoiceQuality voiceQuality) {
            this.voiceQuality = voiceQuality.value();
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public ChannelUpdateRequest build() {
            return new ChannelUpdateRequest(this);
        }
    }
}
