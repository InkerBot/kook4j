package bot.inker.kook4j.entity;

public final class GuildBoostRecord {

    private String userId;
    private String guildId;
    private long startTime;
    private long endTime;
    private User user;

    GuildBoostRecord() {
    }

    public String userId() {
        return userId;
    }

    public String guildId() {
        return guildId;
    }

    public long startTime() {
        return startTime;
    }

    public long endTime() {
        return endTime;
    }

    public User user() {
        return user;
    }

    @Override
    public String toString() {
        return "GuildBoostRecord{userId='" + userId + "', startTime=" + startTime + ", endTime=" + endTime + "}";
    }
}