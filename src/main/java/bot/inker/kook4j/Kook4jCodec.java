package bot.inker.kook4j;

import bot.inker.kook4j.entity.Attachment;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

public final class Kook4jCodec {

    private static final TypeAdapter<Boolean> LENIENT_BOOLEAN = new TypeAdapter<>() {
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            out.value(value);
        }

        @Override
        public Boolean read(JsonReader in) throws IOException {
            var peek = in.peek();
            return switch (peek) {
                case BOOLEAN -> in.nextBoolean();
                case NUMBER -> in.nextInt() != 0;
                case STRING -> Boolean.parseBoolean(in.nextString());
                case NULL -> {
                    in.nextNull();
                    yield false;
                }
                default -> throw new JsonSyntaxException("Expected BOOLEAN but was " + peek);
            };
        }
    };

    // Kook DM API returns attachments as [] when empty, but as {} when present.
    private static final TypeAdapterFactory LENIENT_ATTACHMENT_FACTORY = new TypeAdapterFactory() {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() != Attachment.class) return null;
            var delegate = (TypeAdapter<Attachment>) gson.getDelegateAdapter(this, type);
            return (TypeAdapter<T>) new TypeAdapter<Attachment>() {
                @Override
                public void write(JsonWriter out, Attachment value) throws IOException {
                    delegate.write(out, value);
                }

                @Override
                public Attachment read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    if (in.peek() == JsonToken.BEGIN_ARRAY) {
                        in.beginArray();
                        Attachment result = in.hasNext() ? delegate.read(in) : null;
                        while (in.hasNext()) in.skipValue();
                        in.endArray();
                        return result;
                    }
                    return delegate.read(in);
                }
            };
        }
    };

    private static final Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(boolean.class, LENIENT_BOOLEAN)
            .registerTypeAdapter(Boolean.class, LENIENT_BOOLEAN)
            .registerTypeAdapterFactory(LENIENT_ATTACHMENT_FACTORY)
            .create();

    private Kook4jCodec() {
    }

    public static Gson gson() {
        return GSON;
    }

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    public static <T> T fromJson(JsonElement element, Class<T> type) {
        return GSON.fromJson(element, type);
    }

    public static <T> T fromJson(JsonElement element, Type type) {
        return GSON.fromJson(element, type);
    }

    public static JsonObject toJsonObject(Object obj) {
        return GSON.toJsonTree(obj).getAsJsonObject();
    }

    public static JsonObject parseObject(String json) {
        return GSON.fromJson(json, JsonObject.class);
    }
}