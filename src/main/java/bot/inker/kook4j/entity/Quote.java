package bot.inker.kook4j.entity;

public final class Quote {

    private String id;
    private int type;
    private String content;
    private long createAt;
    private User author;

    Quote() {
    }

    private Quote(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.content = builder.content;
        this.createAt = builder.createAt;
        this.author = builder.author;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String id() {
        return id;
    }

    public int type() {
        return type;
    }

    public String content() {
        return content;
    }

    public long createAt() {
        return createAt;
    }

    public User author() {
        return author;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private String id;
        private int type;
        private String content;
        private long createAt;
        private User author;

        Builder() {
        }

        Builder(Quote src) {
            this.id = src.id;
            this.type = src.type;
            this.content = src.content;
            this.createAt = src.createAt;
            this.author = src.author;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder createAt(long createAt) {
            this.createAt = createAt;
            return this;
        }

        public Builder author(User author) {
            this.author = author;
            return this;
        }

        public Quote build() {
            return new Quote(this);
        }
    }
}
