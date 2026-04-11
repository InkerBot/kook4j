package bot.inker.kook4j.event;

import com.google.gson.JsonObject;

public class SystemEvent extends Event {

    private String eventType;
    private JsonObject body;

    protected SystemEvent() {
    }

    public String eventType() {
        return eventType;
    }

    public JsonObject body() {
        return body;
    }

    public void populate(String eventType, JsonObject body) {
        this.eventType = eventType;
        this.body = body;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{eventType='" + eventType + "'}";
    }
}
