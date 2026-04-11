package bot.inker.kook4j.entity;

import java.util.List;

public final class GuildMuteList {

    private MuteGroup mic;
    private MuteGroup headset;

    GuildMuteList() {
    }

    public MuteGroup mic() {
        return mic;
    }

    public MuteGroup headset() {
        return headset;
    }

    @Override
    public String toString() {
        return "GuildMuteList{mic=" + mic + ", headset=" + headset + "}";
    }

    public static final class MuteGroup {
        private int type;
        private List<String> userIds;

        /**
         * 1 = mic muted, 2 = headset deafened.
         */
        public int type() {
            return type;
        }

        public List<String> userIds() {
            return userIds;
        }

        @Override
        public String toString() {
            return "MuteGroup{type=" + type + ", userIds=" + userIds + "}";
        }
    }
}