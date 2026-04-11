package bot.inker.kook4j.request;

public final class IntimacyUpdateRequest {

    private final Integer score;
    private final String socialInfo;
    private final Integer imgId;

    private IntimacyUpdateRequest(Builder builder) {
        this.score = builder.score;
        this.socialInfo = builder.socialInfo;
        this.imgId = builder.imgId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Integer score() {
        return score;
    }

    public String socialInfo() {
        return socialInfo;
    }

    public Integer imgId() {
        return imgId;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private Integer score;
        private String socialInfo;
        private Integer imgId;

        Builder() {
        }

        Builder(IntimacyUpdateRequest src) {
            this.score = src.score;
            this.socialInfo = src.socialInfo;
            this.imgId = src.imgId;
        }

        public Builder score(int score) {
            this.score = score;
            return this;
        }

        public Builder socialInfo(String socialInfo) {
            this.socialInfo = socialInfo;
            return this;
        }

        public Builder imgId(int imgId) {
            this.imgId = imgId;
            return this;
        }

        public IntimacyUpdateRequest build() {
            return new IntimacyUpdateRequest(this);
        }
    }
}
