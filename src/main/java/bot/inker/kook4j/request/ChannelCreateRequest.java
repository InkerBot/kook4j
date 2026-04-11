package bot.inker.kook4j.request;

import bot.inker.kook4j.ChannelType;
import bot.inker.kook4j.VoiceQuality;

public final class ChannelCreateRequest {

    private final String name;
    private final String parentId;
    private final int type;
    private final int limitAmount;
    private final String voiceQuality;
    private final boolean isCategory;

    private ChannelCreateRequest(Builder builder) {
        this.name = builder.name;
        this.parentId = builder.parentId;
        this.type = builder.type;
        this.limitAmount = builder.limitAmount;
        this.voiceQuality = builder.voiceQuality;
        this.isCategory = builder.isCategory;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String name() {
        return name;
    }

    public String parentId() {
        return parentId;
    }

    public int type() {
        return type;
    }

    public int limitAmount() {
        return limitAmount;
    }

    public String voiceQuality() {
        return voiceQuality;
    }

    public boolean isCategory() {
        return isCategory;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private String name;
        private String parentId;
        private int type = 1;
        private int limitAmount;
        private String voiceQuality;
        private boolean isCategory;

        Builder() {
        }

        Builder(ChannelCreateRequest src) {
            this.name = src.name;
            this.parentId = src.parentId;
            this.type = src.type;
            this.limitAmount = src.limitAmount;
            this.voiceQuality = src.voiceQuality;
            this.isCategory = src.isCategory;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder parentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder type(ChannelType type) {
            this.type = type.value();
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

        public Builder isCategory(boolean isCategory) {
            this.isCategory = isCategory;
            return this;
        }

        public ChannelCreateRequest build() {
            return new ChannelCreateRequest(this);
        }
    }
}
