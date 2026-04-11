package bot.inker.kook4j.event.role;

import bot.inker.kook4j.event.SystemEvent;

public final class RoleDeleteEvent extends SystemEvent {

    private int roleId;
    private String name;
    private int color;
    private int position;
    private int hoist;
    private int mentionable;
    private int permissions;

    RoleDeleteEvent() {
    }

    public int roleId() {
        return roleId;
    }

    public String name() {
        return name;
    }

    public int color() {
        return color;
    }

    public int position() {
        return position;
    }

    public int hoist() {
        return hoist;
    }

    public int mentionable() {
        return mentionable;
    }

    public int permissions() {
        return permissions;
    }
}
