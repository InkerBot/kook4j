package bot.inker.kook4j.entity;

import bot.inker.kook4j.http.HttpClient;
import bot.inker.kook4j.scope.ChannelScope;

import java.util.List;

public final class Channel implements ChannelScope, Bindable {

    private transient volatile HttpClient _http;

    private String id;
    private String guildId;
    private String userId;
    private String parentId;
    private String name;
    private String topic;
    private int type;
    private int level;
    private int slowMode;
    private boolean isCategory;
    private List<PermissionOverwrite> permissionOverwrites;
    private List<PermissionUser> permissionUsers;
    private int permissionSync;
    private boolean hasPassword;
    private int limitAmount;
    private String voiceQuality;
    private String serverUrl;
    private List<String> children;

    Channel() {
    }

    private Channel(Builder builder) {
        this.id = builder.id;
        this.guildId = builder.guildId;
        this.userId = builder.userId;
        this.parentId = builder.parentId;
        this.name = builder.name;
        this.topic = builder.topic;
        this.type = builder.type;
        this.level = builder.level;
        this.slowMode = builder.slowMode;
        this.isCategory = builder.isCategory;
        this.permissionOverwrites = builder.permissionOverwrites;
        this.permissionUsers = builder.permissionUsers;
        this.permissionSync = builder.permissionSync;
        this.hasPassword = builder.hasPassword;
        this.limitAmount = builder.limitAmount;
        this.voiceQuality = builder.voiceQuality;
        this.serverUrl = builder.serverUrl;
        this.children = builder.children;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String id() {
        return id;
    }

    public String guildId() {
        return guildId;
    }

    public String userId() {
        return userId;
    }

    public String parentId() {
        return parentId;
    }

    public String name() {
        return name;
    }

    public String topic() {
        return topic;
    }

    public int type() {
        return type;
    }

    public int level() {
        return level;
    }

    public int slowMode() {
        return slowMode;
    }

    public boolean isCategory() {
        return isCategory;
    }

    public List<PermissionOverwrite> permissionOverwrites() {
        return permissionOverwrites;
    }

    public List<PermissionUser> permissionUsers() {
        return permissionUsers;
    }

    public int permissionSync() {
        return permissionSync;
    }

    public boolean hasPassword() {
        return hasPassword;
    }

    public int limitAmount() {
        return limitAmount;
    }

    public String voiceQuality() {
        return voiceQuality;
    }

    public String serverUrl() {
        return serverUrl;
    }

    public List<String> children() {
        return children;
    }

    public boolean isText() {
        return type == 1;
    }

    public boolean isVoice() {
        return type == 2;
    }

    public Channel bind(HttpClient http) {
        this._http = http;
        return this;
    }

    @Override
    public HttpClient http() {
        return _http;
    }

    @Override
    public String channelId() {
        return id;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "Channel{id='" + id + "', name='" + name + "', type=" + type + "}";
    }

    public static final class PermissionOverwrite {
        private int roleId;
        private int allow;
        private int deny;

        public int roleId() {
            return roleId;
        }

        public int allow() {
            return allow;
        }

        public int deny() {
            return deny;
        }
    }

    public static final class PermissionUser {
        private User user;
        private int allow;
        private int deny;

        public User user() {
            return user;
        }

        public int allow() {
            return allow;
        }

        public int deny() {
            return deny;
        }
    }

    public static final class Builder {
        private String id;
        private String guildId;
        private String userId;
        private String parentId;
        private String name;
        private String topic;
        private int type;
        private int level;
        private int slowMode;
        private boolean isCategory;
        private List<PermissionOverwrite> permissionOverwrites;
        private List<PermissionUser> permissionUsers;
        private int permissionSync;
        private boolean hasPassword;
        private int limitAmount;
        private String voiceQuality;
        private String serverUrl;
        private List<String> children;

        Builder() {
        }

        Builder(Channel src) {
            this.id = src.id;
            this.guildId = src.guildId;
            this.userId = src.userId;
            this.parentId = src.parentId;
            this.name = src.name;
            this.topic = src.topic;
            this.type = src.type;
            this.level = src.level;
            this.slowMode = src.slowMode;
            this.isCategory = src.isCategory;
            this.permissionOverwrites = src.permissionOverwrites;
            this.permissionUsers = src.permissionUsers;
            this.permissionSync = src.permissionSync;
            this.hasPassword = src.hasPassword;
            this.limitAmount = src.limitAmount;
            this.voiceQuality = src.voiceQuality;
            this.serverUrl = src.serverUrl;
            this.children = src.children;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder guildId(String guildId) {
            this.guildId = guildId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder parentId(String parentId) {
            this.parentId = parentId;
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

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder level(int level) {
            this.level = level;
            return this;
        }

        public Builder slowMode(int slowMode) {
            this.slowMode = slowMode;
            return this;
        }

        public Builder isCategory(boolean isCategory) {
            this.isCategory = isCategory;
            return this;
        }

        public Builder permissionOverwrites(List<PermissionOverwrite> permissionOverwrites) {
            this.permissionOverwrites = permissionOverwrites;
            return this;
        }

        public Builder permissionUsers(List<PermissionUser> permissionUsers) {
            this.permissionUsers = permissionUsers;
            return this;
        }

        public Builder permissionSync(int permissionSync) {
            this.permissionSync = permissionSync;
            return this;
        }

        public Builder hasPassword(boolean hasPassword) {
            this.hasPassword = hasPassword;
            return this;
        }

        public Builder limitAmount(int limitAmount) {
            this.limitAmount = limitAmount;
            return this;
        }

        public Builder voiceQuality(String voiceQuality) {
            this.voiceQuality = voiceQuality;
            return this;
        }

        public Builder serverUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        public Builder children(List<String> children) {
            this.children = children;
            return this;
        }

        public Channel build() {
            return new Channel(this);
        }
    }
}
