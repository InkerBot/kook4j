package bot.inker.kook4j.event.user;

import bot.inker.kook4j.event.SystemEvent;

public final class UserUpdateEvent extends SystemEvent {

    private String userId;
    private String username;
    private String avatar;

    UserUpdateEvent() {
    }

    public String userId() {
        return userId;
    }

    public String username() {
        return username;
    }

    public String avatar() {
        return avatar;
    }
}
