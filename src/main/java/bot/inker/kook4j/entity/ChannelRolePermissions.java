package bot.inker.kook4j.entity;

import java.util.List;

public final class ChannelRolePermissions {

    private List<Channel.PermissionOverwrite> permissionOverwrites;
    private List<Channel.PermissionUser> permissionUsers;
    private int permissionSync;

    ChannelRolePermissions() {
    }

    public List<Channel.PermissionOverwrite> permissionOverwrites() {
        return permissionOverwrites;
    }

    public List<Channel.PermissionUser> permissionUsers() {
        return permissionUsers;
    }

    public int permissionSync() {
        return permissionSync;
    }

    @Override
    public String toString() {
        return "ChannelRolePermissions{permissionSync=" + permissionSync + "}";
    }
}