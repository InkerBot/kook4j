package bot.inker.kook4j.event.member;

import bot.inker.kook4j.event.SystemEvent;

import java.util.List;

public final class MemberOfflineEvent extends SystemEvent {

    private String userId;
    private long eventTime;
    private List<String> guilds;

    MemberOfflineEvent() {
    }

    public String userId() {
        return userId;
    }

    public long eventTime() {
        return eventTime;
    }

    public List<String> guilds() {
        return guilds;
    }
}
