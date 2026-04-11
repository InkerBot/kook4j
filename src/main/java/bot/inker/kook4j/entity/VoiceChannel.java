package bot.inker.kook4j.entity;

public final class VoiceChannel {

    private String id;
    private String guildId;
    private String parentId;
    private String name;

    VoiceChannel() {
    }

    private VoiceChannel(Builder builder) {
        this.id = builder.id;
        this.guildId = builder.guildId;
        this.parentId = builder.parentId;
        this.name = builder.name;
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

    public String parentId() {
        return parentId;
    }

    public String name() {
        return name;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "VoiceChannel{id='" + id + "', name='" + name + "'}";
    }

    public static final class Builder {
        private String id;
        private String guildId;
        private String parentId;
        private String name;

        Builder() {
        }

        Builder(VoiceChannel src) {
            this.id = src.id;
            this.guildId = src.guildId;
            this.parentId = src.parentId;
            this.name = src.name;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder guildId(String guildId) {
            this.guildId = guildId;
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

        public VoiceChannel build() {
            return new VoiceChannel(this);
        }
    }
}
