package bot.inker.kook4j;

import bot.inker.kook4j.entity.*;
import bot.inker.kook4j.event.*;
import bot.inker.kook4j.event.channel.*;
import bot.inker.kook4j.event.dm.DirectMessageDeleteEvent;
import bot.inker.kook4j.event.dm.DirectMessageUpdateEvent;
import bot.inker.kook4j.event.dm.DirectReactionAddEvent;
import bot.inker.kook4j.event.dm.DirectReactionRemoveEvent;
import bot.inker.kook4j.event.guild.*;
import bot.inker.kook4j.event.member.*;
import bot.inker.kook4j.event.role.RoleCreateEvent;
import bot.inker.kook4j.event.role.RoleDeleteEvent;
import bot.inker.kook4j.event.role.RoleUpdateEvent;
import bot.inker.kook4j.event.user.*;
import bot.inker.kook4j.http.HttpClient;
import bot.inker.kook4j.scope.*;
import bot.inker.kook4j.ws.KookWebSocket;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class BotInstance {

    private static final Logger log = LoggerFactory.getLogger(BotInstance.class);

    private static final Map<String, Class<? extends SystemEvent>> SYSTEM_EVENTS = Map.ofEntries(
            // Channel events
            Map.entry("added_channel", ChannelCreateEvent.class),
            Map.entry("updated_channel", ChannelUpdateEvent.class),
            Map.entry("deleted_channel", ChannelDeleteEvent.class),
            Map.entry("updated_message", ChannelMessageUpdateEvent.class),
            Map.entry("deleted_message", ChannelMessageDeleteEvent.class),
            Map.entry("added_reaction", ReactionAddEvent.class),
            Map.entry("deleted_reaction", ReactionRemoveEvent.class),
            Map.entry("pinned_message", PinAddEvent.class),
            Map.entry("unpinned_message", PinRemoveEvent.class),
            // Guild events
            Map.entry("updated_guild", GuildUpdateEvent.class),
            Map.entry("deleted_guild", GuildDeleteEvent.class),
            Map.entry("added_block_list", BlockListAddEvent.class),
            Map.entry("deleted_block_list", BlockListRemoveEvent.class),
            Map.entry("added_emoji", EmojiAddEvent.class),
            Map.entry("removed_emoji", EmojiRemoveEvent.class),
            Map.entry("updated_emoji", EmojiUpdateEvent.class),
            // Member events
            Map.entry("joined_guild", MemberJoinEvent.class),
            Map.entry("exited_guild", MemberLeaveEvent.class),
            Map.entry("updated_guild_member", MemberUpdateEvent.class),
            Map.entry("guild_member_online", MemberOnlineEvent.class),
            Map.entry("guild_member_offline", MemberOfflineEvent.class),
            // Role events
            Map.entry("added_role", RoleCreateEvent.class),
            Map.entry("deleted_role", RoleDeleteEvent.class),
            Map.entry("updated_role", RoleUpdateEvent.class),
            // User events
            Map.entry("user_updated", UserUpdateEvent.class),
            Map.entry("joined_channel", VoiceJoinEvent.class),
            Map.entry("exited_channel", VoiceLeaveEvent.class),
            Map.entry("self_joined_guild", BotJoinGuildEvent.class),
            Map.entry("self_exited_guild", BotLeaveGuildEvent.class),
            Map.entry("message_btn_click", ButtonClickEvent.class),
            // DM events
            Map.entry("updated_private_message", DirectMessageUpdateEvent.class),
            Map.entry("deleted_private_message", DirectMessageDeleteEvent.class),
            Map.entry("private_added_reaction", DirectReactionAddEvent.class),
            Map.entry("private_deleted_reaction", DirectReactionRemoveEvent.class)
    );

    private final HttpClient http;
    private final EventBus eventBus;
    private final KookWebSocket webSocket;

    BotInstance(HttpClient http, boolean compress) {
        this.http = http;
        this.eventBus = new EventBus();
        this.webSocket = new KookWebSocket(this, http, compress);
    }

    public void start() {
        webSocket.connect();
    }

    public void stop() {
        webSocket.disconnect();
    }

    public <T extends Event> BotInstance on(Class<T> type, EventListener<T> listener) {
        eventBus.on(type, listener);
        return this;
    }

    public <T extends Event> BotInstance off(Class<T> type, EventListener<T> listener) {
        eventBus.off(type, listener);
        return this;
    }

    public GuildScope guild(String guildId) {
        return new GuildScopeRecord(http, guildId);
    }

    public ChannelScope channel(String channelId) {
        return new ChannelScopeRecord(http, channelId);
    }

    public MessageScope message(String msgId) {
        return new MessageScopeRecord(http, msgId);
    }

    public UserScope user(String userId) {
        return new UserScopeRecord(http, userId);
    }

    public UserChatScope userChat(String chatCode) {
        return new UserChatScopeRecord(http, chatCode);
    }

    public DmScope dm(String targetId) {
        return new DmScope(http, targetId);
    }

    public AssetScope assets() {
        return new AssetScope(http);
    }

    public VoiceScope voice(String channelId) {
        return new VoiceScopeRecord(http, channelId);
    }

    public PagedList<Guild> guilds() {
        return guilds(1, 50);
    }

    public PagedList<Guild> guilds(int page, int pageSize) {
        var data = http.get("/guild/list", Map.of(
                "page", String.valueOf(page),
                "page_size", String.valueOf(pageSize)
        ));
        var obj = data.getAsJsonObject();
        List<Guild> items = Kook4jCodec.fromJson(obj.get("items"), new TypeToken<List<Guild>>() {
        }.getType());
        var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
        return new PagedList<>(items, meta, this::guildsAsync);
    }

    public User me() {
        return Kook4jCodec.fromJson(http.get("/user/me"), User.class);
    }

    public PagedList<UserChat> userChats() {
        return userChats(1, 50);
    }

    public PagedList<UserChat> userChats(int page, int pageSize) {
        var data = http.get("/user-chat/list", Map.of(
                "page", String.valueOf(page),
                "page_size", String.valueOf(pageSize)
        ));
        var obj = data.getAsJsonObject();
        List<UserChat> items = Kook4jCodec.fromJson(obj.get("items"), new TypeToken<List<UserChat>>() {
        }.getType());
        var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
        return new PagedList<>(items, meta, this::userChatsAsync);
    }

    public String uploadAsset(File file) {
        return assets().upload(file);
    }

    public PagedList<VoiceChannel> voiceChannels() {
        return voiceChannels(1, 50);
    }

    public PagedList<VoiceChannel> voiceChannels(int page, int pageSize) {
        var data = http.get("/voice/list", Map.of(
                "page", String.valueOf(page),
                "page_size", String.valueOf(pageSize)
        ));
        var obj = data.getAsJsonObject();
        List<VoiceChannel> items = Kook4jCodec.fromJson(obj.get("items"),
                new TypeToken<List<VoiceChannel>>() {
                }.getType());
        var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
        return new PagedList<>(items, meta, this::voiceChannelsAsync);
    }

    public void offline() {
        http.post("/user/offline", new JsonObject());
    }

    public void online() {
        http.post("/user/online", new JsonObject());
    }

    public OnlineStatus onlineStatus() {
        return Kook4jCodec.fromJson(http.get("/user/get-online-status"), OnlineStatus.class);
    }

    public CompletableFuture<PagedList<Guild>> guildsAsync() {
        return guildsAsync(1, 50);
    }

    public CompletableFuture<PagedList<Guild>> guildsAsync(int page, int pageSize) {
        return http.getAsync("/guild/list", Map.of(
                "page", String.valueOf(page),
                "page_size", String.valueOf(pageSize)
        )).thenApply(data -> {
            var obj = data.getAsJsonObject();
            List<Guild> items = Kook4jCodec.fromJson(obj.get("items"), new TypeToken<List<Guild>>() {
            }.getType());
            var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
            return new PagedList<>(items, meta, this::guildsAsync);
        });
    }

    public CompletableFuture<User> meAsync() {
        return http.getAsync("/user/me")
                .thenApply(data -> Kook4jCodec.fromJson(data, User.class));
    }

    public CompletableFuture<PagedList<UserChat>> userChatsAsync() {
        return userChatsAsync(1, 50);
    }

    public CompletableFuture<PagedList<UserChat>> userChatsAsync(int page, int pageSize) {
        return http.getAsync("/user-chat/list", Map.of(
                "page", String.valueOf(page),
                "page_size", String.valueOf(pageSize)
        )).thenApply(data -> {
            var obj = data.getAsJsonObject();
            List<UserChat> items = Kook4jCodec.fromJson(obj.get("items"), new TypeToken<List<UserChat>>() {
            }.getType());
            var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
            return new PagedList<>(items, meta, this::userChatsAsync);
        });
    }

    public CompletableFuture<String> uploadAssetAsync(File file) {
        return assets().uploadAsync(file);
    }

    public CompletableFuture<PagedList<VoiceChannel>> voiceChannelsAsync() {
        return voiceChannelsAsync(1, 50);
    }

    public CompletableFuture<PagedList<VoiceChannel>> voiceChannelsAsync(int page, int pageSize) {
        return http.getAsync("/voice/list", Map.of(
                "page", String.valueOf(page),
                "page_size", String.valueOf(pageSize)
        )).thenApply(data -> {
            var obj = data.getAsJsonObject();
            List<VoiceChannel> items = Kook4jCodec.fromJson(obj.get("items"),
                    new TypeToken<List<VoiceChannel>>() {
                    }.getType());
            var meta = Kook4jCodec.fromJson(obj.get("meta"), PagedList.Meta.class);
            return new PagedList<>(items, meta, this::voiceChannelsAsync);
        });
    }

    public CompletableFuture<Void> offlineAsync() {
        return http.postAsync("/user/offline", new JsonObject()).thenAccept(__ -> {
        });
    }

    public CompletableFuture<Void> onlineAsync() {
        return http.postAsync("/user/online", new JsonObject()).thenAccept(__ -> {
        });
    }

    public CompletableFuture<OnlineStatus> onlineStatusAsync() {
        return http.getAsync("/user/get-online-status")
                .thenApply(data -> Kook4jCodec.fromJson(data, OnlineStatus.class));
    }

    public HttpClient httpClient() {
        return http;
    }

    public void dispatchEvent(JsonObject data) {
        try {
            var channelType = data.has("channel_type") ? data.get("channel_type").getAsString() : "";
            var type = data.get("type").getAsInt();
            var targetId = data.has("target_id") ? data.get("target_id").getAsString() : "";
            var authorId = data.has("author_id") ? data.get("author_id").getAsString() : "";
            var msgId = data.has("msg_id") ? data.get("msg_id").getAsString() : "";
            var msgTimestamp = data.has("msg_timestamp") ? data.get("msg_timestamp").getAsLong() : 0L;
            var nonce = data.has("nonce") ? data.get("nonce").getAsString() : "";
            var extra = data.has("extra") ? data.getAsJsonObject("extra") : new JsonObject();

            Event event;

            if (type == MessageType.SYSTEM.value()) {
                event = parseSystemEvent(extra);
            } else {
                event = parseMessageEvent(type, data, extra);
            }

            if (event != null) {
                event.inject(this, channelType, targetId, authorId, msgId, msgTimestamp, nonce);
                eventBus.dispatch(event);
            }
        } catch (Exception e) {
            log.error("Failed to dispatch event", e);
        }
    }

    private MessageEvent parseMessageEvent(int type, JsonObject data, JsonObject extra) {
        var event = new MessageEvent();
        var content = data.has("content") ? data.get("content").getAsString() : "";

        User author = null;
        if (extra.has("author")) {
            author = Kook4jCodec.fromJson(extra.get("author"), User.class);
        }

        List<String> mention = null;
        if (extra.has("mention")) {
            mention = Kook4jCodec.fromJson(extra.get("mention"), new TypeToken<List<String>>() {
            }.getType());
        }

        var mentionAll = extra.has("mention_all") && extra.get("mention_all").getAsBoolean();

        List<Integer> mentionRoles = null;
        if (extra.has("mention_roles")) {
            mentionRoles = Kook4jCodec.fromJson(extra.get("mention_roles"), new TypeToken<List<Integer>>() {
            }.getType());
        }

        var mentionHere = extra.has("mention_here") && extra.get("mention_here").getAsBoolean();

        Attachment attachments = null;
        if (extra.has("attachments") && extra.get("attachments").isJsonObject()) {
            attachments = Kook4jCodec.fromJson(extra.get("attachments"), Attachment.class);
        }

        Quote quote = null;
        if (extra.has("quote") && extra.get("quote").isJsonObject()) {
            quote = Kook4jCodec.fromJson(extra.get("quote"), Quote.class);
        }

        event.populate(type, content, author, mention, mentionAll, mentionRoles, mentionHere, attachments, quote);
        return event;
    }

    private SystemEvent parseSystemEvent(JsonObject extra) {
        var eventType = extra.has("type") ? extra.get("type").getAsString() : "";
        var body = extra.has("body") ? extra.getAsJsonObject("body") : new JsonObject();

        var eventClass = SYSTEM_EVENTS.get(eventType);
        SystemEvent event;

        if (eventClass != null) {
            event = Kook4jCodec.fromJson(body, eventClass);
        } else {
            log.debug("Unknown system event type: {}", eventType);
            event = Kook4jCodec.fromJson(body, SystemEvent.class);
        }

        event.populate(eventType, body);
        return event;
    }

    private record GuildScopeRecord(HttpClient http, String guildId) implements GuildScope {
    }

    private record ChannelScopeRecord(HttpClient http, String channelId) implements ChannelScope {
    }

    private record MessageScopeRecord(HttpClient http, String msgId) implements MessageScope {
    }

    private record UserScopeRecord(HttpClient http, String userId) implements UserScope {
    }

    private record UserChatScopeRecord(HttpClient http, String chatCode) implements UserChatScope {
    }

    private record VoiceScopeRecord(HttpClient http, String channelId) implements VoiceScope {
    }
}
