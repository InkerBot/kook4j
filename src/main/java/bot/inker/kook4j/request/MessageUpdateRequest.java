package bot.inker.kook4j.request;

public final class MessageUpdateRequest {

    private final String content;
    private final String quote;
    private final String tempTargetId;
    private final String replyMsgId;

    private MessageUpdateRequest(Builder builder) {
        this.content = builder.content;
        this.quote = builder.quote;
        this.tempTargetId = builder.tempTargetId;
        this.replyMsgId = builder.replyMsgId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String content() {
        return content;
    }

    public String quote() {
        return quote;
    }

    public String tempTargetId() {
        return tempTargetId;
    }

    public String replyMsgId() {
        return replyMsgId;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private String content;
        private String quote;
        private String tempTargetId;
        private String replyMsgId;

        Builder() {
        }

        Builder(MessageUpdateRequest src) {
            this.content = src.content;
            this.quote = src.quote;
            this.tempTargetId = src.tempTargetId;
            this.replyMsgId = src.replyMsgId;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder quote(String quote) {
            this.quote = quote;
            return this;
        }

        public Builder tempTargetId(String tempTargetId) {
            this.tempTargetId = tempTargetId;
            return this;
        }

        public Builder replyMsgId(String replyMsgId) {
            this.replyMsgId = replyMsgId;
            return this;
        }

        public MessageUpdateRequest build() {
            return new MessageUpdateRequest(this);
        }
    }
}
