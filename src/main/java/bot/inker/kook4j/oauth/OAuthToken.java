package bot.inker.kook4j.oauth;

public final class OAuthToken {

    private String accessToken;
    private int expiresIn;
    private String tokenType;
    private String scope;

    OAuthToken() {
    }

    private OAuthToken(Builder builder) {
        this.accessToken = builder.accessToken;
        this.expiresIn = builder.expiresIn;
        this.tokenType = builder.tokenType;
        this.scope = builder.scope;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String accessToken() {
        return accessToken;
    }

    public int expiresIn() {
        return expiresIn;
    }

    public String tokenType() {
        return tokenType;
    }

    public String scope() {
        return scope;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "OAuthToken{tokenType='" + tokenType + "', scope='" + scope +
                "', expiresIn=" + expiresIn + "}";
    }

    public static final class Builder {
        private String accessToken;
        private int expiresIn;
        private String tokenType;
        private String scope;

        Builder() {
        }

        Builder(OAuthToken src) {
            this.accessToken = src.accessToken;
            this.expiresIn = src.expiresIn;
            this.tokenType = src.tokenType;
            this.scope = src.scope;
        }

        public Builder accessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder expiresIn(int expiresIn) {
            this.expiresIn = expiresIn;
            return this;
        }

        public Builder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public Builder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public OAuthToken build() {
            return new OAuthToken(this);
        }
    }
}
