package bot.inker.kook4j.entity;

import bot.inker.kook4j.http.HttpClient;
import bot.inker.kook4j.scope.UserScope;

import java.util.List;

public final class User implements UserScope, Bindable {

    private transient volatile HttpClient _http;

    private String id;
    private String username;
    private String identifyNum;
    private String nickname;
    private boolean online;
    private String os;
    private int status;
    private String avatar;
    private String vipAvatar;
    private String banner;
    private List<Integer> roles;
    private boolean isVip;
    private boolean vipAmp;
    private boolean bot;
    private int botStatus;
    private boolean mobileVerified;
    private boolean isSys;
    private String clientId;
    private boolean verified;
    private String mobilePrefix;
    private String mobile;
    private int invitedCount;
    private long joinedAt;
    private long activeTime;

    User() {
    }

    private User(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.identifyNum = builder.identifyNum;
        this.nickname = builder.nickname;
        this.online = builder.online;
        this.os = builder.os;
        this.status = builder.status;
        this.avatar = builder.avatar;
        this.vipAvatar = builder.vipAvatar;
        this.banner = builder.banner;
        this.roles = builder.roles;
        this.isVip = builder.isVip;
        this.vipAmp = builder.vipAmp;
        this.bot = builder.bot;
        this.botStatus = builder.botStatus;
        this.mobileVerified = builder.mobileVerified;
        this.isSys = builder.isSys;
        this.clientId = builder.clientId;
        this.verified = builder.verified;
        this.mobilePrefix = builder.mobilePrefix;
        this.mobile = builder.mobile;
        this.invitedCount = builder.invitedCount;
        this.joinedAt = builder.joinedAt;
        this.activeTime = builder.activeTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String identifyNum() {
        return identifyNum;
    }

    public String nickname() {
        return nickname;
    }

    public boolean online() {
        return online;
    }

    public String os() {
        return os;
    }

    public int status() {
        return status;
    }

    public String avatar() {
        return avatar;
    }

    public String vipAvatar() {
        return vipAvatar;
    }

    public String banner() {
        return banner;
    }

    public List<Integer> roles() {
        return roles;
    }

    public boolean isVip() {
        return isVip;
    }

    public boolean vipAmp() {
        return vipAmp;
    }

    public boolean bot() {
        return bot;
    }

    public int botStatus() {
        return botStatus;
    }

    public boolean mobileVerified() {
        return mobileVerified;
    }

    public boolean isSys() {
        return isSys;
    }

    public String clientId() {
        return clientId;
    }

    public boolean verified() {
        return verified;
    }

    public String mobilePrefix() {
        return mobilePrefix;
    }

    public String mobile() {
        return mobile;
    }

    public int invitedCount() {
        return invitedCount;
    }

    public long joinedAt() {
        return joinedAt;
    }

    public long activeTime() {
        return activeTime;
    }

    public User bind(HttpClient http) {
        this._http = http;
        return this;
    }

    @Override
    public HttpClient http() {
        return _http;
    }

    @Override
    public String userId() {
        return id;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', username='" + username + "#" + identifyNum + "'}";
    }

    public static final class Builder {
        private String id;
        private String username;
        private String identifyNum;
        private String nickname;
        private boolean online;
        private String os;
        private int status;
        private String avatar;
        private String vipAvatar;
        private String banner;
        private List<Integer> roles;
        private boolean isVip;
        private boolean vipAmp;
        private boolean bot;
        private int botStatus;
        private boolean mobileVerified;
        private boolean isSys;
        private String clientId;
        private boolean verified;
        private String mobilePrefix;
        private String mobile;
        private int invitedCount;
        private long joinedAt;
        private long activeTime;

        Builder() {
        }

        Builder(User src) {
            this.id = src.id;
            this.username = src.username;
            this.identifyNum = src.identifyNum;
            this.nickname = src.nickname;
            this.online = src.online;
            this.os = src.os;
            this.status = src.status;
            this.avatar = src.avatar;
            this.vipAvatar = src.vipAvatar;
            this.banner = src.banner;
            this.roles = src.roles;
            this.isVip = src.isVip;
            this.vipAmp = src.vipAmp;
            this.bot = src.bot;
            this.botStatus = src.botStatus;
            this.mobileVerified = src.mobileVerified;
            this.isSys = src.isSys;
            this.clientId = src.clientId;
            this.verified = src.verified;
            this.mobilePrefix = src.mobilePrefix;
            this.mobile = src.mobile;
            this.invitedCount = src.invitedCount;
            this.joinedAt = src.joinedAt;
            this.activeTime = src.activeTime;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder identifyNum(String identifyNum) {
            this.identifyNum = identifyNum;
            return this;
        }

        public Builder nickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public Builder online(boolean online) {
            this.online = online;
            return this;
        }

        public Builder os(String os) {
            this.os = os;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder vipAvatar(String vipAvatar) {
            this.vipAvatar = vipAvatar;
            return this;
        }

        public Builder banner(String banner) {
            this.banner = banner;
            return this;
        }

        public Builder roles(List<Integer> roles) {
            this.roles = roles;
            return this;
        }

        public Builder isVip(boolean isVip) {
            this.isVip = isVip;
            return this;
        }

        public Builder vipAmp(boolean vipAmp) {
            this.vipAmp = vipAmp;
            return this;
        }

        public Builder bot(boolean bot) {
            this.bot = bot;
            return this;
        }

        public Builder botStatus(int botStatus) {
            this.botStatus = botStatus;
            return this;
        }

        public Builder mobileVerified(boolean mobileVerified) {
            this.mobileVerified = mobileVerified;
            return this;
        }

        public Builder isSys(boolean isSys) {
            this.isSys = isSys;
            return this;
        }

        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public Builder verified(boolean verified) {
            this.verified = verified;
            return this;
        }

        public Builder mobilePrefix(String mobilePrefix) {
            this.mobilePrefix = mobilePrefix;
            return this;
        }

        public Builder mobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public Builder invitedCount(int invitedCount) {
            this.invitedCount = invitedCount;
            return this;
        }

        public Builder joinedAt(long joinedAt) {
            this.joinedAt = joinedAt;
            return this;
        }

        public Builder activeTime(long activeTime) {
            this.activeTime = activeTime;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
