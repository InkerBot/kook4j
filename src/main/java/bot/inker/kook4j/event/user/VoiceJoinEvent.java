package bot.inker.kook4j.event.user;

import bot.inker.kook4j.event.SystemEvent;

public final class VoiceJoinEvent extends SystemEvent {

    private String userId;
    private String channelId;

    VoiceJoinEvent() {
    }

    public String userId() {
        return userId;
    }

    public String channelId() {
        return channelId;
    }
}
