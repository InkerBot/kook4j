package bot.inker.kook4j.entity;

public final class Role {

    private int roleId;
    private String name;
    private int color;
    private int position;
    private int hoist;
    private int mentionable;
    private int permissions;

    Role() {
    }

    private Role(Builder builder) {
        this.roleId = builder.roleId;
        this.name = builder.name;
        this.color = builder.color;
        this.position = builder.position;
        this.hoist = builder.hoist;
        this.mentionable = builder.mentionable;
        this.permissions = builder.permissions;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int roleId() {
        return roleId;
    }

    public String name() {
        return name;
    }

    public int color() {
        return color;
    }

    public int position() {
        return position;
    }

    public int hoist() {
        return hoist;
    }

    public int mentionable() {
        return mentionable;
    }

    public int permissions() {
        return permissions;
    }

    public boolean hasPermission(int bit) {
        return (permissions & (1 << bit)) != 0;
    }

    public boolean isAdmin() {
        return hasPermission(0);
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "Role{roleId=" + roleId + ", name='" + name + "'}";
    }

    public static final class Builder {
        private int roleId;
        private String name;
        private int color;
        private int position;
        private int hoist;
        private int mentionable;
        private int permissions;

        Builder() {
        }

        Builder(Role src) {
            this.roleId = src.roleId;
            this.name = src.name;
            this.color = src.color;
            this.position = src.position;
            this.hoist = src.hoist;
            this.mentionable = src.mentionable;
            this.permissions = src.permissions;
        }

        public Builder roleId(int roleId) {
            this.roleId = roleId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder position(int position) {
            this.position = position;
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

        public Role build() {
            return new Role(this);
        }
    }
}
