package bot.inker.kook4j.entity;

public final class GuildEmoji {

    private String id;
    private String name;
    private String userId;
    private String guildId;

    GuildEmoji() {
    }

    private GuildEmoji(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.userId = builder.userId;
        this.guildId = builder.guildId;
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

    public String userId() {
        return userId;
    }

    public String guildId() {
        return guildId;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "GuildEmoji{id='" + id + "', name='" + name + "'}";
    }

    public static final class Builder {
        private String id;
        private String name;
        private String userId;
        private String guildId;

        Builder() {
        }

        Builder(GuildEmoji src) {
            this.id = src.id;
            this.name = src.name;
            this.userId = src.userId;
            this.guildId = src.guildId;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder guildId(String guildId) {
            this.guildId = guildId;
            return this;
        }

        public GuildEmoji build() {
            return new GuildEmoji(this);
        }
    }
}
