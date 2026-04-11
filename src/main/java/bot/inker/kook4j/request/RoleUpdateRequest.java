package bot.inker.kook4j.request;

public final class RoleUpdateRequest {

    private final String name;
    private final Integer color;
    private final Integer hoist;
    private final Integer mentionable;
    private final Integer permissions;

    private RoleUpdateRequest(Builder builder) {
        this.name = builder.name;
        this.color = builder.color;
        this.hoist = builder.hoist;
        this.mentionable = builder.mentionable;
        this.permissions = builder.permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String name() {
        return name;
    }

    public Integer color() {
        return color;
    }

    public Integer hoist() {
        return hoist;
    }

    public Integer mentionable() {
        return mentionable;
    }

    public Integer permissions() {
        return permissions;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private String name;
        private Integer color;
        private Integer hoist;
        private Integer mentionable;
        private Integer permissions;

        Builder() {
        }

        Builder(RoleUpdateRequest src) {
            this.name = src.name;
            this.color = src.color;
            this.hoist = src.hoist;
            this.mentionable = src.mentionable;
            this.permissions = src.permissions;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder hoist(int hoist) {
            this.hoist = hoist;
            return this;
        }

        public Builder mentionable(int mentionable) {
            this.mentionable = mentionable;
            return this;
        }

        public Builder permissions(int permissions) {
            this.permissions = permissions;
            return this;
        }

        public RoleUpdateRequest build() {
            return new RoleUpdateRequest(this);
        }
    }
}
