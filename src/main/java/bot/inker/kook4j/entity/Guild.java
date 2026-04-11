package bot.inker.kook4j.entity;

import bot.inker.kook4j.http.HttpClient;
import bot.inker.kook4j.scope.GuildScope;

import java.util.List;

public final class Guild implements GuildScope {

    private transient volatile HttpClient _http;

    private String id;
    private String name;
    private String topic;
    private String userId;
    private String icon;
    private int notifyType;
    private String region;
    private boolean enableOpen;
    private String openId;
    private String defaultChannelId;
    private String welcomeChannelId;
    private int boostNum;
    private int level;
    private List<Role> roles;
    private List<Channel> channels;

    Guild() {
    }

    private Guild(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.topic = builder.topic;
        this.userId = builder.userId;
        this.icon = builder.icon;
        this.notifyType = builder.notifyType;
        this.region = builder.region;
        this.enableOpen = builder.enableOpen;
        this.openId = builder.openId;
        this.defaultChannelId = builder.defaultChannelId;
        this.welcomeChannelId = builder.welcomeChannelId;
        this.boostNum = builder.boostNum;
        this.level = builder.level;
        this.roles = builder.roles;
        this.channels = builder.channels;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String topic() {
        return topic;
    }

    public String userId() {
        return userId;
    }

    public String icon() {
        return icon;
    }

    public int notifyType() {
        return notifyType;
    }

    public String region() {
        return region;
    }

    public boolean enableOpen() {
        return enableOpen;
    }

    public String openId() {
        return openId;
    }

    public String defaultChannelId() {
        return defaultChannelId;
    }

    public String welcomeChannelId() {
        return welcomeChannelId;
    }

    public int boostNum() {
        return boostNum;
    }

    public int level() {
        return level;
    }

    public List<Role> roles() {
        return roles;
    }

    public List<Channel> channels() {
        return channels;
    }

    public Guild bind(HttpClient http) {
        this._http = http;
        return this;
    }

    @Override
    public HttpClient http() {
        return _http;
    }

    @Override
    public String guildId() {
        return id;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "Guild{id='" + id + "', name='" + name + "'}";
    }

    public static final class Builder {
        private String id;
        private String name;
        private String topic;
        private String userId;
        private String icon;
        private int notifyType;
        private String region;
        private boolean enableOpen;
        private String openId;
        private String defaultChannelId;
        private String welcomeChannelId;
        private int boostNum;
        private int level;
        private List<Role> roles;
        private List<Channel> channels;

        Builder() {
        }

        Builder(Guild src) {
            this.id = src.id;
            this.name = src.name;
            this.topic = src.topic;
            this.userId = src.userId;
            this.icon = src.icon;
            this.notifyType = src.notifyType;
            this.region = src.region;
            this.enableOpen = src.enableOpen;
            this.openId = src.openId;
            this.defaultChannelId = src.defaultChannelId;
            this.welcomeChannelId = src.welcomeChannelId;
            this.boostNum = src.boostNum;
            this.level = src.level;
            this.roles = src.roles;
            this.channels = src.channels;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder icon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder notifyType(int notifyType) {
            this.notifyType = notifyType;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder enableOpen(boolean enableOpen) {
            this.enableOpen = enableOpen;
            return this;
        }

        public Builder openId(String openId) {
            this.openId = openId;
            return this;
        }

        public Builder defaultChannelId(String defaultChannelId) {
            this.defaultChannelId = defaultChannelId;
            return this;
        }

        public Builder welcomeChannelId(String welcomeChannelId) {
            this.welcomeChannelId = welcomeChannelId;
            return this;
        }

        public Builder boostNum(int boostNum) {
            this.boostNum = boostNum;
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public Builder roles(List<Role> roles) {
            this.roles = roles;
            return this;
        }

        public Builder channels(List<Channel> channels) {
            this.channels = channels;
            return this;
        }

        public Guild build() {
            return new Guild(this);
        }
    }
}
