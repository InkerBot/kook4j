package bot.inker.kook4j.event.member;

import bot.inker.kook4j.event.SystemEvent;

import java.util.List;

public final class MemberOnlineEvent extends SystemEvent {

    private String userId;
    private long eventTime;
    private List<String> guilds;

    MemberOnlineEvent() {
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
