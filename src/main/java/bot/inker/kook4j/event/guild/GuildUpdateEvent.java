package bot.inker.kook4j.event.guild;

import bot.inker.kook4j.event.SystemEvent;

public final class GuildUpdateEvent extends SystemEvent {

    private String id;
    private String name;
    private String userId;
    private String icon;
    private int notifyType;
    private String region;
    private boolean enableOpen;
    private String openId;
    private String defaultChannelId;
    private String welcomeChannelId;

    GuildUpdateEvent() {
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

    public String icon() {
        return icon;
    }

    public int notifyType() {
        return notifyType;
    }

    public String region() {
        return region;
    }

    public boolean enableOpen() {
        return enableOpen;
    }

    public String openId() {
        return openId;
    }

    public String defaultChannelId() {
        return defaultChannelId;
    }

    public String welcomeChannelId() {
        return welcomeChannelId;
    }
}
