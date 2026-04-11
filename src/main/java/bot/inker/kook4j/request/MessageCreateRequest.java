package bot.inker.kook4j.request;

import bot.inker.kook4j.MessageType;

public final class MessageCreateRequest {

    private final int type;
    private final String content;
    private final String quote;
    private final String nonce;
    private final String tempTargetId;
    private final String replyMsgId;

    private MessageCreateRequest(Builder builder) {
        this.type = builder.type;
        this.content = builder.content;
        this.quote = builder.quote;
        this.nonce = builder.nonce;
        this.tempTargetId = builder.tempTargetId;
        this.replyMsgId = builder.replyMsgId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int type() {
        return type;
    }

    public String content() {
        return content;
    }

    public String quote() {
        return quote;
    }

    public String nonce() {
        return nonce;
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
        private int type = MessageType.KMARKDOWN.value();
        private String content;
        private String quote;
        private String nonce;
        private String tempTargetId;
        private String replyMsgId;

        Builder() {
        }

        Builder(MessageCreateRequest src) {
            this.type = src.type;
            this.content = src.content;
            this.quote = src.quote;
            this.nonce = src.nonce;
            this.tempTargetId = src.tempTargetId;
            this.replyMsgId = src.replyMsgId;
        }

        public Builder type(MessageType type) {
            this.type = type.value();
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder quote(String quote) {
            this.quote = quote;
            return this;
        }

        public Builder nonce(String nonce) {
            this.nonce = nonce;
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

        public MessageCreateRequest build() {
            return new MessageCreateRequest(this);
        }
    }
}
