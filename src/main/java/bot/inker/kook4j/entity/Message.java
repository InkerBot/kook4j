package bot.inker.kook4j.entity;

import bot.inker.kook4j.http.HttpClient;
import bot.inker.kook4j.scope.MessageScope;

import java.util.List;

public final class Message implements MessageScope, Bindable {

    private transient volatile HttpClient _http;

    private String id;
    private int type;
    private User author;
    private String content;
    private List<String> mention;
    private boolean mentionAll;
    private List<Integer> mentionRoles;
    private boolean mentionHere;
    private List<Object> embeds;
    private Attachment attachments;
    private List<Reaction> reactions;
    private Quote quote;
    private long createAt;
    private long updatedAt;
    private String nonce;

    Message() {
    }

    private Message(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.author = builder.author;
        this.content = builder.content;
        this.mention = builder.mention;
        this.mentionAll = builder.mentionAll;
        this.mentionRoles = builder.mentionRoles;
        this.mentionHere = builder.mentionHere;
        this.embeds = builder.embeds;
        this.attachments = builder.attachments;
        this.reactions = builder.reactions;
        this.quote = builder.quote;
        this.createAt = builder.createAt;
        this.updatedAt = builder.updatedAt;
        this.nonce = builder.nonce;
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

    public User author() {
        return author;
    }

    public String content() {
        return content;
    }

    public List<String> mention() {
        return mention;
    }

    public boolean mentionAll() {
        return mentionAll;
    }

    public List<Integer> mentionRoles() {
        return mentionRoles;
    }

    public boolean mentionHere() {
        return mentionHere;
    }

    public List<Object> embeds() {
        return embeds;
    }

    public Attachment attachments() {
        return attachments;
    }

    public List<Reaction> reactions() {
        return reactions;
    }

    public Quote quote() {
        return quote;
    }

    public long createAt() {
        return createAt;
    }

    public long updatedAt() {
        return updatedAt;
    }

    public String nonce() {
        return nonce;
    }

    public Message bind(HttpClient http) {
        this._http = http;
        return this;
    }

    @Override
    public HttpClient http() {
        return _http;
    }

    @Override
    public String msgId() {
        return id;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "Message{id='" + id + "', type=" + type + ", content='" +
                (content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content) + "'}";
    }

    public static final class Builder {
        private String id;
        private int type;
        private User author;
        private String content;
        private List<String> mention;
        private boolean mentionAll;
        private List<Integer> mentionRoles;
        private boolean mentionHere;
        private List<Object> embeds;
        private Attachment attachments;
        private List<Reaction> reactions;
        private Quote quote;
        private long createAt;
        private long updatedAt;
        private String nonce;

        Builder() {
        }

        Builder(Message src) {
            this.id = src.id;
            this.type = src.type;
            this.author = src.author;
            this.content = src.content;
            this.mention = src.mention;
            this.mentionAll = src.mentionAll;
            this.mentionRoles = src.mentionRoles;
            this.mentionHere = src.mentionHere;
            this.embeds = src.embeds;
            this.attachments = src.attachments;
            this.reactions = src.reactions;
            this.quote = src.quote;
            this.createAt = src.createAt;
            this.updatedAt = src.updatedAt;
            this.nonce = src.nonce;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder author(User author) {
            this.author = author;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder mention(List<String> mention) {
            this.mention = mention;
            return this;
        }

        public Builder mentionAll(boolean mentionAll) {
            this.mentionAll = mentionAll;
            return this;
        }

        public Builder mentionRoles(List<Integer> mentionRoles) {
            this.mentionRoles = mentionRoles;
            return this;
        }

        public Builder mentionHere(boolean mentionHere) {
            this.mentionHere = mentionHere;
            return this;
        }

        public Builder embeds(List<Object> embeds) {
            this.embeds = embeds;
            return this;
        }

        public Builder attachments(Attachment attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder reactions(List<Reaction> reactions) {
            this.reactions = reactions;
            return this;
        }

        public Builder quote(Quote quote) {
            this.quote = quote;
            return this;
        }

        public Builder createAt(long createAt) {
            this.createAt = createAt;
            return this;
        }

        public Builder updatedAt(long updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder nonce(String nonce) {
            this.nonce = nonce;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}
