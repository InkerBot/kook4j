package bot.inker.kook4j.entity;

public final class ChannelRoleEntry {

    private String userId;
    private Integer roleId;
    private int allow;
    private int deny;

    ChannelRoleEntry() {
    }

    public String userId() {
        return userId;
    }

    public Integer roleId() {
        return roleId;
    }

    public int allow() {
        return allow;
    }

    public int deny() {
        return deny;
    }

    @Override
    public String toString() {
        return "ChannelRoleEntry{userId='" + userId + "', roleId=" + roleId
                + ", allow=" + allow + ", deny=" + deny + "}";
    }
}