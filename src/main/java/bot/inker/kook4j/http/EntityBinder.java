package bot.inker.kook4j.http;

import bot.inker.kook4j.entity.Bindable;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class EntityBinder {

    private EntityBinder() {
    }

    public static <T> T bindAll(T root, HttpClient http) {
        if (root == null || http == null) return root;
        visit(root, http, Collections.newSetFromMap(new IdentityHashMap<>()));
        return root;
    }

    private static void visit(Object o, HttpClient http, Set<Object> seen) {
        if (o == null || !seen.add(o)) return;

        if (o instanceof Bindable b) {
            b.bind(http);
        }

        if (o instanceof Iterable<?> it) {
            for (var e : it) visit(e, http, seen);
            return;
        }
        if (o instanceof Map<?, ?> m) {
            for (var e : m.values()) visit(e, http, seen);
            return;
        }
        var cls = o.getClass();
        if (cls.isArray()) {
            if (cls.getComponentType().isPrimitive()) return;
            int len = Array.getLength(o);
            for (int i = 0; i < len; i++) visit(Array.get(o, i), http, seen);
            return;
        }

        if (!cls.getPackageName().startsWith("bot.inker.kook4j")) return;

        for (Class<?> c = cls; c != null && c.getPackageName().startsWith("bot.inker.kook4j"); c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                int mods = f.getModifiers();
                if (Modifier.isStatic(mods)) continue;
                var type = f.getType();
                if (type.isPrimitive() || type == String.class) continue;
                try {
                    f.setAccessible(true);
                    visit(f.get(o), http, seen);
                } catch (IllegalAccessException | RuntimeException ignored) {
                }
            }
        }
    }
}