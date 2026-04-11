package bot.inker.kook4j.entity;

public final class Attachment {

    private String type;
    private String url;
    private String name;
    private long size;
    private String fileType;
    private double duration;
    private int width;
    private int height;

    Attachment() {
    }

    private Attachment(Builder builder) {
        this.type = builder.type;
        this.url = builder.url;
        this.name = builder.name;
        this.size = builder.size;
        this.fileType = builder.fileType;
        this.duration = builder.duration;
        this.width = builder.width;
        this.height = builder.height;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String type() {
        return type;
    }

    public String url() {
        return url;
    }

    public String name() {
        return name;
    }

    public long size() {
        return size;
    }

    public String fileType() {
        return fileType;
    }

    public double duration() {
        return duration;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "Attachment{type='" + type + "', name='" + name + "'}";
    }

    public static final class Builder {
        private String type;
        private String url;
        private String name;
        private long size;
        private String fileType;
        private double duration;
        private int width;
        private int height;

        Builder() {
        }

        Builder(Attachment src) {
            this.type = src.type;
            this.url = src.url;
            this.name = src.name;
            this.size = src.size;
            this.fileType = src.fileType;
            this.duration = src.duration;
            this.width = src.width;
            this.height = src.height;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder size(long size) {
            this.size = size;
            return this;
        }

        public Builder fileType(String fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder duration(double duration) {
            this.duration = duration;
            return this;
        }

        public Builder width(int width) {
            this.width = width;
            return this;
        }

        public Builder height(int height) {
            this.height = height;
            return this;
        }

        public Attachment build() {
            return new Attachment(this);
        }
    }
}
