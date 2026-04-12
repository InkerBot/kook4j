package bot.inker.kook4j.entity;

import bot.inker.kook4j.http.HttpClient;
import bot.inker.kook4j.scope.UserChatScope;

public final class UserChat implements UserChatScope, Bindable {

    private transient volatile HttpClient _http;

    private String code;
    private long lastReadTime;
    private long latestMsgTime;
    private int unreadCount;
    private boolean isFriend;
    private boolean isBlocked;
    private boolean isTargetBlocked;
    private User targetInfo;

    UserChat() {
    }

    private UserChat(Builder builder) {
        this.code = builder.code;
        this.lastReadTime = builder.lastReadTime;
        this.latestMsgTime = builder.latestMsgTime;
        this.unreadCount = builder.unreadCount;
        this.isFriend = builder.isFriend;
        this.isBlocked = builder.isBlocked;
        this.isTargetBlocked = builder.isTargetBlocked;
        this.targetInfo = builder.targetInfo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String code() {
        return code;
    }

    public long lastReadTime() {
        return lastReadTime;
    }

    public long latestMsgTime() {
        return latestMsgTime;
    }

    public int unreadCount() {
        return unreadCount;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public boolean isTargetBlocked() {
        return isTargetBlocked;
    }

    public User targetInfo() {
        return targetInfo;
    }

    public UserChat bind(HttpClient http) {
        this._http = http;
        return this;
    }

    @Override
    public HttpClient http() {
        return _http;
    }

    @Override
    public String chatCode() {
        return code;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "UserChat{code='" + code + "'}";
    }

    public static final class Builder {
        private String code;
        private long lastReadTime;
        private long latestMsgTime;
        private int unreadCount;
        private boolean isFriend;
        private boolean isBlocked;
        private boolean isTargetBlocked;
        private User targetInfo;

        Builder() {
        }

        Builder(UserChat src) {
            this.code = src.code;
            this.lastReadTime = src.lastReadTime;
            this.latestMsgTime = src.latestMsgTime;
            this.unreadCount = src.unreadCount;
            this.isFriend = src.isFriend;
            this.isBlocked = src.isBlocked;
            this.isTargetBlocked = src.isTargetBlocked;
            this.targetInfo = src.targetInfo;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder lastReadTime(long lastReadTime) {
            this.lastReadTime = lastReadTime;
            return this;
        }

        public Builder latestMsgTime(long latestMsgTime) {
            this.latestMsgTime = latestMsgTime;
            return this;
        }

        public Builder unreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
            return this;
        }

        public Builder isFriend(boolean isFriend) {
            this.isFriend = isFriend;
            return this;
        }

        public Builder isBlocked(boolean isBlocked) {
            this.isBlocked = isBlocked;
            return this;
        }

        public Builder isTargetBlocked(boolean isTargetBlocked) {
            this.isTargetBlocked = isTargetBlocked;
            return this;
        }

        public Builder targetInfo(User targetInfo) {
            this.targetInfo = targetInfo;
            return this;
        }

        public UserChat build() {
            return new UserChat(this);
        }
    }
}
