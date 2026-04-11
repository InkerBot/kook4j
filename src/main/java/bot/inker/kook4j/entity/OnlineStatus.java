package bot.inker.kook4j.entity;

import java.util.List;

public final class OnlineStatus {

    private boolean online;
    private List<String> onlineOs;

    OnlineStatus() {
    }

    public boolean online() {
        return online;
    }

    public List<String> onlineOs() {
        return onlineOs;
    }

    @Override
    public String toString() {
        return "OnlineStatus{online=" + online + ", onlineOs=" + onlineOs + "}";
    }
}