package bot.inker.kook4j.event;

import bot.inker.kook4j.BotInstance;

public abstract class Event {

    private transient BotInstance bot;
    // These envelope fields are injected after body parsing, so Gson must ignore them.
    private transient String channelType;
    private transient String targetId;
    private transient String authorId;
    private transient String msgId;
    private transient long msgTimestamp;
    private transient String nonce;

    protected Event() {
    }

    public BotInstance bot() {
        return bot;
    }

    public String channelType() {
        return channelType;
    }

    public String targetId() {
        return targetId;
    }

    public String authorId() {
        return authorId;
    }

    public String msgId() {
        return msgId;
    }

    public long msgTimestamp() {
        return msgTimestamp;
    }

    public String nonce() {
        return nonce;
    }

    public boolean isGroup() {
        return "GROUP".equals(channelType);
    }

    public boolean isPerson() {
        return "PERSON".equals(channelType);
    }

    public boolean isBroadcast() {
        return "BROADCAST".equals(channelType);
    }

    public void inject(BotInstance bot, String channelType, String targetId, String authorId,
                       String msgId, long msgTimestamp, String nonce) {
        this.bot = bot;
        this.channelType = channelType;
        this.targetId = targetId;
        this.authorId = authorId;
        this.msgId = msgId;
        this.msgTimestamp = msgTimestamp;
        this.nonce = nonce;
    }
}
