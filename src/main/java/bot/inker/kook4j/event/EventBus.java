package bot.inker.kook4j.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class EventBus {

    private static final Logger log = LoggerFactory.getLogger(EventBus.class);

    private final ConcurrentHashMap<Class<? extends Event>, List<EventListener<?>>> listeners =
            new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Event> void on(Class<T> type, EventListener<T> listener) {
        listeners.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public <T extends Event> void off(Class<T> type, EventListener<T> listener) {
        var list = listeners.get(type);
        if (list != null) {
            list.remove(listener);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void dispatch(Event event) {
        Class<?> type = event.getClass();
        while (type != null && Event.class.isAssignableFrom(type)) {
            var list = listeners.get(type);
            if (list != null) {
                for (EventListener listener : list) {
                    try {
                        listener.onEvent(event);
                    } catch (Exception e) {
                        log.error("Error dispatching event {} to listener", event.getClass().getSimpleName(), e);
                    }
                }
            }
            type = type.getSuperclass();
        }
    }
}
