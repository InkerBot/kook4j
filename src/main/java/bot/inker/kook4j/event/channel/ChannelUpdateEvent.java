package bot.inker.kook4j.event.channel;

import bot.inker.kook4j.event.SystemEvent;

public final class ChannelUpdateEvent extends SystemEvent {

    private String id;
    private String guildId;
    private String userId;
    private String parentId;
    private String name;
    private int type;
    private int level;
    private int slowMode;
    private int limitAmount;
    private boolean isCategory;

    ChannelUpdateEvent() {
    }

    public String id() {
        return id;
    }

    public String guildId() {
        return guildId;
    }

    public String userId() {
        return userId;
    }

    public String parentId() {
        return parentId;
    }

    public String name() {
        return name;
    }

    public int type() {
        return type;
    }

    public int level() {
        return level;
    }

    public int slowMode() {
        return slowMode;
    }

    public int limitAmount() {
        return limitAmount;
    }

    public boolean isCategory() {
        return isCategory;
    }
}
